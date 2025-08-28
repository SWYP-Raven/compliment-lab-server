package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByFriendIdOrderByCreatedAtAsc(Long friendId, Pageable pageable);

    Optional<Chat> findTopByFriendIdOrderByCreatedAtDesc(Long friendId);
  
    @Query("SELECT c FROM Chat c " +
            "WHERE c.friend = :friend " +
            "AND (:lastCreatedAt IS NULL OR c.createdAt < :lastCreatedAt) " +
            "ORDER BY c.createdAt DESC")
    Slice<Chat> findNextChats(@Param("friend") Friend friend,
                              @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
                              Pageable pageable);

    @Query("SELECT c FROM Chat c " +
            "WHERE c.friend = :friend " +
            "ORDER BY c.createdAt DESC")
    List<Chat> findLastChats(@Param("friend") Friend friend, Pageable pageable);


    Optional<Chat> findFirstByFriendOrderByCreatedAtDesc(Friend friend);
}
