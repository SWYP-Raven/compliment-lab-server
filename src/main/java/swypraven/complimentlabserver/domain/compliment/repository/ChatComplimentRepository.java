package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatComplimentRepository extends JpaRepository<ChatCompliment, Long> {

    // ===== 조회 =====

    /**
     * 사용자별 채팅 칭찬 조회 (최신순)
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend", "user"})
    Page<ChatCompliment> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    /**
     * 사용자별 특정 채팅 칭찬 조회
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend", "user"})
    Optional<ChatCompliment> findByIdAndUserId(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    /**
     * 커서 기반 페이징 조회 (무한 스크롤용)
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    Slice<ChatCompliment> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("cursorAt") Instant cursorAt,
            Pageable pageable
    );

    /**
     * 기간별 조회
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    Page<ChatCompliment> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    // ===== 검색 =====

    /**
     * 키워드 검색 (제목, 내용, 원본 메시지)
     */
    // ChatComplimentRepository.java
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    @Query("""
       SELECT cc
       FROM ChatCompliment cc
       JOIN cc.chat c
       WHERE cc.user.id = :userId
         AND (
           :keyword IS NULL OR :keyword = '' OR
           cc.title   LIKE CONCAT('%', :keyword, '%') OR
           cc.content LIKE CONCAT('%', :keyword, '%') OR
           c.message  LIKE CONCAT('%', :keyword, '%')
         )
       ORDER BY cc.createdAt DESC
       """)
    Page<ChatCompliment> searchByUserAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    /**
     * 고급 검색 (제목, 내용, 태그, 기간)
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    @Query("""
           SELECT cc
           FROM ChatCompliment cc
           WHERE cc.user.id = :userId
             AND (:title IS NULL OR LOWER(cc.title) LIKE LOWER(CONCAT('%', :title, '%')))
             AND (:content IS NULL OR LOWER(cc.content) LIKE LOWER(CONCAT('%', :content, '%')))
             AND (:startDate IS NULL OR cc.createdAt >= :startDate)
             AND (:endDate IS NULL OR cc.createdAt <= :endDate)
           ORDER BY cc.createdAt DESC
           """)
    Page<ChatCompliment> searchAdvanced(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("content") String content,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );

    // ===== 통계 =====

    /**
     * 사용자별 총 개수
     */
    @Query("SELECT COUNT(cc) FROM ChatCompliment cc WHERE cc.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 사용자별 기간 내 개수
     */
    @Query("""
           SELECT COUNT(cc) 
           FROM ChatCompliment cc 
           WHERE cc.user.id = :userId 
             AND cc.createdAt BETWEEN :startDate AND :endDate
           """)
    long countByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    /**
     * 채팅별 칭찬 존재 여부
     */
    boolean existsByChatIdAndUserId(
            @Param("chatId") Long chatId,
            @Param("userId") Long userId
    );

    // ===== 삭제 =====

    /**
     * 사용자별 특정 칭찬 삭제
     */
    @Modifying
    @Query("DELETE FROM ChatCompliment cc WHERE cc.user.id = :userId AND cc.id = :id")
    int deleteByUserIdAndId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    /**
     * 사용자별 전체 삭제
     */
    @Modifying
    @Query("DELETE FROM ChatCompliment cc WHERE cc.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 기간별 일괄 삭제
     */
    @Modifying
    @Query("""
           DELETE FROM ChatCompliment cc 
           WHERE cc.user.id = :userId 
             AND cc.createdAt BETWEEN :startDate AND :endDate
           """)
    int deleteByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    // ===== 벌크 작업 =====

    /**
     * 여러 ID로 일괄 조회
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    @Query("SELECT cc FROM ChatCompliment cc WHERE cc.id IN :ids AND cc.user.id = :userId")
    List<ChatCompliment> findAllByIdsAndUserId(
            @Param("ids") List<Long> ids,
            @Param("userId") Long userId
    );

    /**
     * 여러 채팅에 대한 칭찬 조회
     */
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    @Query("SELECT cc FROM ChatCompliment cc WHERE cc.chat.id IN :chatIds AND cc.user.id = :userId")
    List<ChatCompliment> findAllByChatIdsAndUserId(
            @Param("chatIds") List<Long> chatIds,
            @Param("userId") Long userId
    );
    // ChatComplimentRepository.java
    @EntityGraph(attributePaths = {"chat", "chat.friend"})
    @Query("""
    select cc
    from ChatCompliment cc
    where cc.user.id = :userId
      and (:lastCreatedAt is null or cc.createdAt < :lastCreatedAt)
    order by cc.createdAt desc
""")
    Slice<ChatCompliment> findNextChats(
            @Param("userId") Long userId,
            @Param("lastCreatedAt") Instant lastCreatedAt,
            Pageable pageable
    );

}