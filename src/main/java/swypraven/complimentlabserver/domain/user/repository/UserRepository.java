package swypraven.complimentlabserver.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // JpaRepository에 이미 findById가 있음 → 삭제 권장
    // Optional<User> findById(Long id);

    Optional<User> findByAppleSub(String appleSub);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    // UserRepository
    boolean existsByAppleSub(String appleSub);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
