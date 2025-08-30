package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.repository.TodayComplimentRepository;

import java.time.*;

@Service
@RequiredArgsConstructor
public class ComplimentService {
    private final TodayComplimentRepository todayRepo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public TodayDto getTodayOrThrow() {
        LocalDate today = LocalDate.now(KST);
        Instant start = today.atStartOfDay(KST).toInstant();
        Instant end   = today.plusDays(1).atStartOfDay(KST).toInstant();

        TodayCompliment tc = todayRepo
                .findTopByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .orElseThrow(() -> new IllegalArgumentException("TodayCompliment not found"));

        return TodayDto.from(tc);
    }
}
