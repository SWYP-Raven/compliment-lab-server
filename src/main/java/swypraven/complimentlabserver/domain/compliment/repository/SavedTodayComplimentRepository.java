package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedTodayComplimentRepository extends JpaRepository<SavedTodayCompliment, Long> {


    boolean existsByUserIdAndTodayComplimentId(Long userId, Long todayId);

    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    Page<SavedTodayCompliment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long deleteByUserIdAndId(Long userId, Long id);

    long countByUserId(Long userId);

    /** 커서 페이징: createdAt < cursorAt */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    Page<SavedTodayCompliment> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            Long userId, Instant cursorAt, Pageable pageable);

    Optional<SavedTodayCompliment> findByUserIdAndTodayComplimentId(Long userId, Long todayId);

}
