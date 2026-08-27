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
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
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
import swypraven.complimentlabserver.global.exception.archive.ArchiveErrorCode;
import swypraven.complimentlabserver.global.exception.archive.ArchiveException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplimentServiceImpl implements ComplimentService {

    private final ComplimentRepository complimentRepo;
    private final UserComplimentLogRepository logRepo;
    private final UserRepository userRepo;
    private final ComplimentSequenceProvider seq; // seed 기반 순열 계산
    
    private static final int TYPE_COUNT = 5; //캐릭터 유형 타입 개수

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    //타입 랜덤
    private int deterministicType(int userSeed, LocalDate date) {
        // 유저 seed와 날짜를 섞어 고정 시드 생성
        long combined = ((long) userSeed << 32) ^ date.toEpochDay();
        Random r = new Random(combined);

        // 1..TYPE_COUNT 범위의 정수 반환 (DB type 이 int 라면 이게 딱 맞음)
        return 1 + r.nextInt(TYPE_COUNT);
    }
    /** 오늘의 칭찬 (seed 기반) */
    @Override
    @Transactional(readOnly = true)
    public TodayDto getTodayForUser(Long userId) {
        // (인터페이스에 default가 있다면 여기서 위임해도 됩니다)
        LocalDate today = LocalDate.now(KST);
        return getTodayForUserOn(userId, today);
    }


    @Override
    @Transactional(readOnly = true)
    public TodayDto getTodayForUserOn(Long userId, LocalDate date) {
        int seed = userSeed(userId);
        int complId = seq.idFor(seed, date);

        Compliment compl = complimentRepo.findById(complId)
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.TODAY_NOT_FOUND));

        UserComplimentLog log = logRepo.findByUserIdAndDate(userId, date).orElse(null);

        // ✅ type 을 DB값이 아니라 seed 기반 결정값으로
        int typeForThisDay = deterministicType(seed, date);

        return TodayDto.of(
                date,
                compl.getId().longValue(),
                compl.getContent(),
                String.valueOf(typeForThisDay),
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

        Map<LocalDate, Integer> dayToComplId = days.stream()
                .collect(Collectors.toMap(Function.identity(), d -> seq.idFor(seed, d)));

        List<Compliment> compls = complimentRepo.findAllById(dayToComplId.values());
        Map<Integer, Compliment> complMap = compls.stream()
                .collect(Collectors.toMap(Compliment::getId, Function.identity()));

        Map<LocalDate, UserComplimentLog> logs = logRepo.findByUserIdAndDateIn(userId, days).stream()
                .collect(Collectors.toMap(UserComplimentLog::getDate, Function.identity()));

        List<DayComplimentDto> result = days.stream().map(d -> {
            Integer cid = dayToComplId.get(d);
            Compliment c = complMap.get(cid);
            if (c == null) {
                log.warn("Compliment master missing for id={} (date={})", cid, d);
                throw new ArchiveException(ArchiveErrorCode.TODAY_NOT_FOUND);            }

            // ✅ 각 날짜의 type 도 seed 기반으로 고정 랜덤
//            int typeForThisDay = deterministicType(seed, d);

            UserComplimentLog row = logs.get(d);
            return new DayComplimentDto(
                    d,
                    new ComplimentDto(c.getId(), c.getContent(), c.getType()),
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
                throw new ArchiveException(ArchiveErrorCode.TODAY_NOT_FOUND);
            }
            return new DayComplimentDto(
                    log.getDate(),
                    new ComplimentDto(c.getId(), c.getContent(), String.valueOf(c.getType())), // ✅ FIX
                    Boolean.TRUE.equals(log.getIsRead()),
                    Boolean.TRUE.equals(log.getIsArchived())
            );
        }).toList();

        // 4) 응답
        return new ComplimentListResponse(rows);
    }

    /** 유저의 seed (null이면 예외) */
    private int userSeed(Long userId) {
        return userRepo.findById(userId)
                .map(u -> {
                    Integer seed = u.getSeed();
                    if (seed == null) {
                        throw new ArchiveException(ArchiveErrorCode.USER_NOT_FOUND);
                    }
                    return seed;
                })
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.USER_NOT_FOUND));
    }
}
