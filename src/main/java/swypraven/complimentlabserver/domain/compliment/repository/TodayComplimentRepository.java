package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {
    // KST 오늘 0시~내일 0시 범위에서 최신 1건
    Optional<TodayCompliment> findTopByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);


    // 유저가 아직 받지 않은 칭찬 중 랜덤 1건(중복 제외)
    @Query(value = """
        SELECT t.*
        FROM today_compliment t
        WHERE NOT EXISTS (
          SELECT 1 FROM duplicated_compliment d
          WHERE d.user_id = :userId AND d.compliment_id = t.id
        ) 
        """, nativeQuery = true)
    List<TodayCompliment> findAllNotDuplicated(@Param("userId") Long userId);
}
