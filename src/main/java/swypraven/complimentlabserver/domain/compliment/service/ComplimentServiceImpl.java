package swypraven.complimentlabserver.domain.compliment.service;

// Lombok / Spring
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Spring Data
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

// Java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

// Domain - model/dto/response
import swypraven.complimentlabserver.domain.compliment.model.dto.ComplimentDto;
import swypraven.complimentlabserver.domain.compliment.model.dto.DayComplimentDto;
import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;

// Domain - entity
import swypraven.complimentlabserver.domain.compliment.entity.Compliment;
import swypraven.complimentlabserver.domain.compliment.entity.UserComplimentLog;

// Domain - repository
import swypraven.complimentlabserver.domain.compliment.repository.ComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.repository.UserComplimentLogRepository;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;

// Domain - sequence
import swypraven.complimentlabserver.domain.compliment.sequence.ComplimentSequenceProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplimentServiceImpl implements ComplimentService {

    private final ComplimentRepository complimentRepo;
    private final UserComplimentLogRepository logRepo;
    private final UserRepository userRepo;
    private final ComplimentSequenceProvider seq; // seed 기반 순열 계산

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 오늘의 칭찬 (seed 기반) */
    @Override
    @Transactional(readOnly = true)
    public TodayDto getTodayForUser(Long userId) {
        LocalDate today = LocalDate.now(KST);
        int complId = seq.idFor(userSeed(userId), today);

        Compliment compl = complimentRepo.findById(complId)
                .orElseThrow(() -> new NoSuchElementException("Compliment not found: " + complId));

        UserComplimentLog log = logRepo.findByUserIdAndDate(userId, today).orElse(null);

        return TodayDto.of(
                today,
                compl.getId().longValue(),                // Compliment PK(Integer) → Long
                compl.getContent(),
                String.valueOf(compl.getType()),          // Enum이면 .name() 사용 가능
                log != null && Boolean.TRUE.equals(log.getIsRead()),
                log != null && Boolean.TRUE.equals(log.getIsArchived())
        );
    }

    /** 월별 조회 */
    @Override
    @Transactional(readOnly = true)
    public ComplimentListResponse getMonth(Long userId, YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return getRange(userId, start, end);
    }

    /** 범위 조회 (주간 포함) */
    @Override
    @Transactional(readOnly = true)
    public ComplimentListResponse getRange(Long userId, LocalDate start, LocalDate end) {
        List<LocalDate> days = start.datesUntil(end.plusDays(1)).collect(Collectors.toList());

        int seed = userSeed(userId);

        // 날짜별 ComplimentId 계산
        Map<LocalDate, Integer> dayToComplId = days.stream()
                .collect(Collectors.toMap(Function.identity(), d -> seq.idFor(seed, d)));

        // Compliment 묶음 조회 후 맵핑
        List<Compliment> compls = complimentRepo.findAllById(dayToComplId.values());
        // ⭐ Compliment.getId()가 Integer라면 키 타입은 Integer로!
        Map<Integer, Compliment> complMap = compls.stream()
                .collect(Collectors.toMap(Compliment::getId, Function.identity()));


        // 로그 조회
        Map<LocalDate, UserComplimentLog> logs = logRepo.findByUserIdAndDateIn(userId, days).stream()
                .collect(Collectors.toMap(UserComplimentLog::getDate, Function.identity()));

        // 응답 조립
        List<DayComplimentDto> result = days.stream().map(d -> {
            Integer cid = dayToComplId.get(d);
            Compliment c = complMap.get(cid); // ✅ 같은 Integer 타입
            if (c == null) {
                log.warn("Compliment master missing for id={} (date={})", cid, d);
                throw new NoSuchElementException("Compliment not found: " + cid);
            }
            UserComplimentLog row = logs.get(d);
            return new DayComplimentDto(
                    d,  // LocalDate 그대로
                    new ComplimentDto(Math.toIntExact(c.getId()), c.getContent(), String.valueOf(c.getType())),
                    row != null && Boolean.TRUE.equals(row.getIsRead()),
                    row != null && Boolean.TRUE.equals(row.getIsArchived())
            );
        }).collect(Collectors.toList());

        return new ComplimentListResponse(result);
    }

    /** 상태 로그 upsert */
    @Override
    @Transactional
    public void upsertLog(Long userId, LocalDate date, boolean isRead, boolean isArchived) {
        int complId = seq.idFor(userSeed(userId), date);

        UserComplimentLog logRow = logRepo.findByUserIdAndDate(userId, date)
                .orElseGet(() -> {
                    UserComplimentLog created = new UserComplimentLog();
                    created.setUserId(userId);
                    created.setDate(date);
                    created.setComplimentId(complId);
                    created.setIsRead(false);
                    created.setIsArchived(false);
                    return created;
                });

        logRow.setIsRead(isRead);
        logRow.setIsArchived(isArchived);
        logRepo.save(logRow);
    }

    /** 아카이브 월별 조회 */
    @Override
    @Transactional(readOnly = true)
    public ComplimentListResponse getArchivedByMonth(Long userId, YearMonth ym, int page, int size) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        // 1) 아카이브된 로그 페이지 조회
        Page<UserComplimentLog> logsPage = logRepo.findByUserIdAndIsArchivedTrueAndDateBetween(
                userId, start, end, pageable
        );

        // 2) 필요한 칭찬 마스터 일괄 로딩
        List<Integer> complIds = logsPage.stream()
                .map(UserComplimentLog::getComplimentId)
                .distinct()
                .toList();

        Map<Integer, Compliment> complMap = complimentRepo.findAllById(complIds).stream()
                .collect(Collectors.toMap(Compliment::getId, Function.identity()));

        // 3) DTO 매핑
        List<DayComplimentDto> rows = logsPage.stream().map(log -> {
            Compliment c = complMap.get(log.getComplimentId());
            if (c == null) {
                throw new NoSuchElementException("Compliment not found: " + log.getComplimentId());
            }
            return new DayComplimentDto(
                    log.getDate(),
                    new ComplimentDto(Math.toIntExact(c.getId()), c.getContent(), String.valueOf(c.getType())),
                    Boolean.TRUE.equals(log.getIsRead()),
                    Boolean.TRUE.equals(log.getIsArchived())
            );
        }).toList();

        // 4) 응답 (기존 타입 유지)
        return new ComplimentListResponse(rows);
    }


    /** 유저의 seed (null이면 예외) */
    private int userSeed(Long userId) {
        return userRepo.findById(userId)
                .map(u -> {
                    Integer seed = u.getSeed();
                    if (seed == null) throw new NoSuchElementException("User seed is null: " + userId);
                    return seed;
                })
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }
}
