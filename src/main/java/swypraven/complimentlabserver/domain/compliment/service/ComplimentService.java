// ComplimentService.java
package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.entity.DuplicatedCompliment;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.repository.DuplicatedComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.repository.TodayComplimentRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplimentService {

    private final TodayComplimentRepository todayRepo;
    private final DuplicatedComplimentRepository dupRepo;
    private final UserRepository userRepo;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** KST 기준 오늘의 칭찬 (없으면 예외) */
    @Transactional(readOnly = true)
    public TodayDto getTodayOrThrow() {
        LocalDate today = LocalDate.now(KST);
        return getByDateOrThrow(today);
    }

    /** KST 기준 특정 날짜의 칭찬 (없으면 예외) */
    @Transactional(readOnly = true)
    public TodayDto getByDateOrThrow(LocalDate dateKst) {
        LocalDateTime start = dateKst.atStartOfDay(); // 00:00:00
        LocalDateTime end = dateKst.plusDays(1).atStartOfDay(); // 다음 날 00:00:00

        TodayCompliment tc = todayRepo
                .findTopByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .orElseThrow(() -> new TodayNotFoundException("오늘의 칭찬이 없습니다. date=" + dateKst));

        return TodayDto.from(tc);
    }

    /** ✅ 유저에게 아직 주지 않은 칭찬을 랜덤으로 하나 지급 + 지급 로그 저장 */
//    @Transactional
//    public TodayDto pickRandomForUser(Long userId) {
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException("User not found"));
//
//        TodayCompliment picked = todayRepo.pickRandomNotDuplicated(userId)
//                .orElseThrow(() -> new NoSuchElementException("더 이상 줄 수 있는 랜덤 칭찬이 없습니다."));
//
//        // 중복로그 저장(처음 지급이면 exists가 false일 것)
//        if (!dupRepo.existsByUserIdAndComplimentId(userId, picked.getId())) {
//            DuplicatedCompliment log = DuplicatedCompliment.builder()
//                    .user(user)
//                    .compliment(picked)
//                    .isRead(false)
//                    .build();
//            dupRepo.save(log);
//        }
//
//        return TodayDto.from(picked);
//    }

    @Transactional
    public TodayDto pickRandomForUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<TodayCompliment> candidates = todayRepo.findAllNotDuplicated(userId);

        if (candidates.isEmpty()) {
            throw new NoSuchElementException("더 이상 줄 수 있는 랜덤 칭찬이 없습니다.");
        }

        Collections.shuffle(candidates); // 진짜 랜덤
        TodayCompliment picked = candidates.get(0);


        // 중복로그 저장(처음 지급이면 exists가 false일 것)
        if (!dupRepo.existsByUserIdAndComplimentId(userId, picked.getId())) {
            DuplicatedCompliment log = DuplicatedCompliment.builder()
                    .user(user)
                    .compliment(picked)
                    .isRead(false)
                    .build();
            dupRepo.save(log);
        }

        return TodayDto.from(picked);
    }

    /** ✅ 유저의 랜덤 칭찬 로그 전체 읽음 처리 */
    @Transactional
    public void markAllRandomLogsRead(Long userId) {
        dupRepo.markAllAsRead(userId);
    }

    // 커스텀 예외 (원한다면 공용 예외 패키지로 이동)
    public static class TodayNotFoundException extends RuntimeException {
        public TodayNotFoundException(String message) { super(message); }
    }
}
