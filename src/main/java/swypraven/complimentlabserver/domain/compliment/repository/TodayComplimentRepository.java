package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.Instant;
import java.util.Optional;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {
    // KST 오늘 0시~내일 0시 범위에서 최신 1건
    Optional<TodayCompliment> findTopByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);

    // 유저가 아직 받지 않은 칭찬 중 랜덤 1건(중복 제외)
    @Query(value = """
        SELECT tc.*
        FROM compliment_lab.today_compliment tc
        WHERE NOT EXISTS (
          SELECT 1 FROM compliment_lab.duplicated_compliment d
          WHERE d.user_id = :userId
            AND d.compliment_id = tc.id
        )
        ORDER BY RAND()
        LIMIT 1
        """, nativeQuery = true)
    Optional<TodayCompliment> pickRandomNotDuplicated(@Param("userId") Long userId);
}
