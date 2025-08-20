package swypraven.complimentlabserver.domain.user.repository;

import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {

    public Optional<Object> findUserEmailByToken(String refreshToken) {
        return null;
    }

    public void deleteByToken(String refreshToken) {
    }

    public void save(String userEmail, String refreshToken) {
    }

    public void deleteAllByUserEmail(String userEmail) {
    }
}
