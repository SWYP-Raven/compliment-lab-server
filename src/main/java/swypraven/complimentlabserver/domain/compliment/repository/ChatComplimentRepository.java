package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;

import java.time.Instant;

@Repository
public interface ChatComplimentRepository extends JpaRepository<ChatCompliment, Long> {

    @EntityGraph(attributePaths = {"chat"})
    Page<ChatCompliment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long deleteByUserIdAndId(Long userId, Long id);

    long countByUserId(Long userId);

    @EntityGraph(attributePaths = {"chat"})
    Page<ChatCompliment> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(Long userId, Instant cursorAt, Pageable pageable);

    /** 카드 검색(옵션): 카드 원문 대화의 message로 LIKE 검색 */
    @EntityGraph(attributePaths = {"chat"})
    @Query("""
           select cc
           from ChatCompliment cc
           join cc.chat c
           where cc.user.id = :userId
             and (:q is null or :q = '' or lower(c.message) like lower(concat('%', :q, '%')))
           order by cc.createdAt desc
           """)
    Page<ChatCompliment> searchByUserAndChatMessage(@Param("userId") Long userId,
                                                    @Param("q") String q,
                                                    Pageable pageable);
}
