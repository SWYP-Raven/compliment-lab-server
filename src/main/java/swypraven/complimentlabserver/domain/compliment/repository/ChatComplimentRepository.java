package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;

import java.time.Instant;

@Repository
public interface ChatComplimentRepository extends JpaRepository<ChatCompliment, Long> {

    /** 내 카드 최신순 */
    @EntityGraph(attributePaths = {"chat","user"})
    Page<ChatCompliment> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            Pageable pageable
    );

    /** 키워드 검색 (message + 원문 chat.message) */
    @Query("""
       SELECT cc
       FROM ChatCompliment cc
       JOIN cc.chat c
       WHERE cc.user.id = :userId
         AND (
           :keyword IS NULL OR :keyword = '' OR
           cc.message LIKE CONCAT('%', :keyword, '%') OR
           c.message  LIKE CONCAT('%', :keyword, '%')
         )
       ORDER BY cc.createdAt DESC
       """)
    Page<ChatCompliment> searchByUserAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    /** 무한스크롤: 커서(Instant) 이전 것들 */
    @EntityGraph(attributePaths = {"chat","user"})
    @Query("""
           SELECT cc
           FROM ChatCompliment cc
           WHERE cc.user.id = :userId
             AND (:cursor IS NULL OR cc.createdAt < :cursor)
           ORDER BY cc.createdAt DESC
           """)
    Slice<ChatCompliment> findNextChats(
            @Param("userId") Long userId,
            @Param("cursor") Instant cursor,
            Pageable pageable
    );

    /** 삭제 */
    @Modifying
    @Query("DELETE FROM ChatCompliment cc WHERE cc.user.id = :userId AND cc.id = :id")
    int deleteByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);
}
