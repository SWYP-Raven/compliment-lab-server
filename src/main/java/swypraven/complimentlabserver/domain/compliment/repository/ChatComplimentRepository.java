package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;

public interface ChatComplimentRepository extends JpaRepository<ChatCompliment, Long> {
}
