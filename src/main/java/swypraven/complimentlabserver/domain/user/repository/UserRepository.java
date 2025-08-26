package swypraven.complimentlabserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByAppleSub(String appleSub);

    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    // UserRepository
    boolean existsByAppleSub(String appleSub);


}
