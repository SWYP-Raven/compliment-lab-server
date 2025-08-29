package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;

import java.time.Instant;

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

    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    @Query("""
    select s
    from SavedTodayCompliment s
    join s.todayCompliment t
    where s.user.id = :userId
      and (:fromStart is null or t.createdAt >= :fromStart)
      and t.createdAt < :toEndExclusive
    order by s.createdAt desc
""")
    Page<SavedTodayCompliment> findHistory(@Param("userId") Long userId,
                                           @Param("fromStart") Instant fromStart,
                                           @Param("toEndExclusive") Instant toEndExclusive,
                                           Pageable pageable);

}
