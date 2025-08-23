package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.awt.print.Pageable;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.friend = :friend ORDER BY c.createdAt LIMIT 20")
    List<Chat> findChatsByFriend(@Param("friend") Friend friend);

//    @Query("SELECT c FROM Chat c WHERE c.friend = :friend ORDER BY c.createdAt DESC")
//    Pageable<Chat> findAllByFriend(@Param("friend") Friend friend, Pageable pageable);;
}
