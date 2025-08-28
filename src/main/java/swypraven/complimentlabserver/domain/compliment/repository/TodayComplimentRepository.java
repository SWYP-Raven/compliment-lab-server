package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.Instant;
import java.util.Optional;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {
    // KST 오늘 0시~내일 0시 범위에서 최신 1건
    Optional<TodayCompliment> findTopByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);
}
