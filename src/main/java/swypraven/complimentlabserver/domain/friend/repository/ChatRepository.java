package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.friend.entity.Chat;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByFriendIdOrderByCreatedAtAsc(Long friendId, Pageable pageable);

    Optional<Chat> findTopByFriendIdOrderByCreatedAtDesc(Long friendId);
}
