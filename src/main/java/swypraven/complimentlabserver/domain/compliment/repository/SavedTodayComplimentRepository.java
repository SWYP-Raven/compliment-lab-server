package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedTodayComplimentRepository extends JpaRepository<SavedTodayCompliment, Long> {

    // ===== 조회 =====

    /** 사용자별 저장된 오늘의 칭찬 목록(최신순) */
    @EntityGraph(attributePaths = {"user"}) // ✅ 예전 todayCompliment 관련 경로 제거
    Page<SavedTodayCompliment> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    /** 사용자별 특정 저장 항목 조회 */
    @EntityGraph(attributePaths = {"user"}) // ✅ 정리
    Optional<SavedTodayCompliment> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    /** 기간별 조회 (포함/배타 경계는 서비스에서 결정) */
    @EntityGraph(attributePaths = {"user"})
    Page<SavedTodayCompliment>
    findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("startInclusive") Instant startInclusive,
            @Param("endInclusive") Instant endInclusive,
            Pageable pageable
    );

    /** 커서 기반 페이징 (무한 스크롤) */
    @EntityGraph(attributePaths = {"user"})
    Page<SavedTodayCompliment>
    findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("cursorAt") Instant cursorAt,
            Pageable pageable
    );

    /** 고급 히스토리 조회 (배타 upper bound) */
    @Query("""
       SELECT s
       FROM SavedTodayCompliment s
       WHERE s.user.id = :userId
         AND (:fromStart IS NULL OR s.createdAt >= :fromStart)
         AND s.createdAt < :toEndExclusive
       ORDER BY s.createdAt DESC
       """)
    Page<SavedTodayCompliment> findHistory(
            @Param("userId") Long userId,
            @Param("fromStart") Instant fromStart,
            @Param("toEndExclusive") Instant toEndExclusive,
            Pageable pageable
    );

    // ===== 존재 여부 확인 =====
    /** (선택) text+seed 중복 방지에 쓰고 싶다면 유지, 아니면 삭제 가능 */
    boolean existsByUserIdAndTextAndSeed(
            @Param("userId") Long userId,
            @Param("text") String text,
            @Param("seed") Long seed
    );

    boolean existsByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    // ===== 통계 =====
    @Query("SELECT COUNT(s) FROM SavedTodayCompliment s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("""
           SELECT COUNT(s)
           FROM SavedTodayCompliment s
           WHERE s.user.id = :userId
             AND s.createdAt BETWEEN :startDate AND :endDate
           """)
    long countByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // ===== 삭제 =====
    @Modifying
    @Query("DELETE FROM SavedTodayCompliment s WHERE s.user.id = :userId AND s.id = :id")
    int deleteByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    @Modifying
    @Query("DELETE FROM SavedTodayCompliment s WHERE s.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
           DELETE FROM SavedTodayCompliment s
           WHERE s.user.id = :userId
             AND s.createdAt BETWEEN :startDate AND :endDate
           """)
    int deleteByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // ===== 벌크 조회/최근 N개 =====
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT s FROM SavedTodayCompliment s WHERE s.id IN :ids AND s.user.id = :userId")
    List<SavedTodayCompliment> findAllByIdsAndUserId(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );

    @EntityGraph(attributePaths = {"user"})
    @Query("""
           SELECT s
           FROM SavedTodayCompliment s
           WHERE s.user.id = :userId
           ORDER BY s.createdAt DESC
           """)
    List<SavedTodayCompliment> findRecentByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
