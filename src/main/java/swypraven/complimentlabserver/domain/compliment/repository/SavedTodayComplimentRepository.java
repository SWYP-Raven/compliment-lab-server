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

    /**
     * 사용자별 저장된 오늘의 칭찬 목록 조회 (최신순)
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type", "user"})
    Page<SavedTodayCompliment> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 사용자별 특정 저장 항목 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    Optional<SavedTodayCompliment> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    /**
     * 기간별 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    Page<SavedTodayCompliment> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("startInclusive") Instant startInclusive,
            @Param("endInclusive") Instant endInclusive,
            Pageable pageable
    );

    /**
     * 커서 기반 페이징 (무한 스크롤)
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    Page<SavedTodayCompliment> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("cursorAt") Instant cursorAt,
            Pageable pageable
    );

    /**
     * 고급 히스토리 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    @Query("""
           SELECT s
           FROM SavedTodayCompliment s
           JOIN s.todayCompliment t
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

    /**
     * 중복 저장 체크
     */
    boolean existsByUserIdAndTodayComplimentId(
            @Param("userId") Long userId,
            @Param("todayId") Long todayId
    );

    /**
     * 사용자별 저장 여부
     */
    boolean existsByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    // ===== 통계 =====

    /**
     * 사용자별 총 저장 개수
     */
    @Query("SELECT COUNT(s) FROM SavedTodayCompliment s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 기간별 저장 개수
     */
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

    /**
     * 특정 타입별 저장 개수
     */
    @Query("""
           SELECT COUNT(s) 
           FROM SavedTodayCompliment s 
           WHERE s.user.id = :userId 
             AND s.todayCompliment.type.id = :typeId
           """)
    long countByUserIdAndTypeId(
            @Param("userId") Long userId,
            @Param("typeId") Long typeId
    );

    // ===== 삭제 =====

    /**
     * 사용자별 특정 항목 삭제
     */
    @Modifying
    @Query("DELETE FROM SavedTodayCompliment s WHERE s.user.id = :userId AND s.id = :id")
    int deleteByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    /**
     * 사용자별 전체 삭제
     */
    @Modifying
    @Query("DELETE FROM SavedTodayCompliment s WHERE s.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 기간별 일괄 삭제
     */
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

    // ===== 벌크 조회 =====

    /**
     * 여러 ID로 일괄 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    @Query("SELECT s FROM SavedTodayCompliment s WHERE s.id IN :ids AND s.user.id = :userId")
    List<SavedTodayCompliment> findAllByIdsAndUserId(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );

    /**
     * 최근 N개 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
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

    /**
     * 타입별 조회
     */
    @EntityGraph(attributePaths = {"todayCompliment", "todayCompliment.type"})
    @Query("""
           SELECT s 
           FROM SavedTodayCompliment s 
           WHERE s.user.id = :userId 
             AND s.todayCompliment.type.id = :typeId
           ORDER BY s.createdAt DESC
           """)
    Page<SavedTodayCompliment> findByUserIdAndTypeId(
            @Param("userId") Long userId,
            @Param("typeId") Long typeId,
            Pageable pageable
    );
}