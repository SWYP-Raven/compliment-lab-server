package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.repository.TodayComplimentRepository;

import java.time.*;

@Service
@RequiredArgsConstructor
public class ComplimentService {

    private final TodayComplimentRepository todayRepo;
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
        Instant start = dateKst.atStartOfDay(KST).toInstant();
        Instant end   = dateKst.plusDays(1).atStartOfDay(KST).toInstant();

        TodayCompliment tc = todayRepo
                .findTopByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .orElseThrow(() -> new TodayNotFoundException("오늘의 칭찬이 없습니다. date=" + dateKst));

        return TodayDto.from(tc);
    }

    /** 선택: 없으면 null 반환하고 호출부에서 처리하고 싶다면 */
    @Transactional(readOnly = true)
    public TodayDto getTodayOrNull() {
        LocalDate today = LocalDate.now(KST);
        Instant start = today.atStartOfDay(KST).toInstant();
        Instant end   = today.plusDays(1).atStartOfDay(KST).toInstant();

        return todayRepo.findTopByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .map(TodayDto::from)
                .orElse(null);
    }

    // 커스텀 예외 (원한다면 공용 예외 패키지로 이동)
    public static class TodayNotFoundException extends RuntimeException {
        public TodayNotFoundException(String message) { super(message); }
    }
}
