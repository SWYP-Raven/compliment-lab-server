package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatComplimentRepository extends JpaRepository<ChatCompliment, Long> {

    @Query("SELECT c FROM ChatCompliment c " +
            "WHERE c.user = :user " +
            "AND (:lastCreatedAt IS NULL OR c.createdAt < :lastCreatedAt) " +
            "ORDER BY c.createdAt DESC")
    Slice<ChatCompliment> findNextChats(@Param("user") User user,
                              @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
                              Pageable pageable);


    List<ChatCompliment> findAllByUser(User user);
}
