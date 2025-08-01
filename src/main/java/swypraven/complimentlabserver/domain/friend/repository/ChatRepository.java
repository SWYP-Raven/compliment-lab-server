package swypraven.complimentlabserver.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.friend.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
