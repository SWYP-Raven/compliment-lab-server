package swypraven.complimentlabserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
