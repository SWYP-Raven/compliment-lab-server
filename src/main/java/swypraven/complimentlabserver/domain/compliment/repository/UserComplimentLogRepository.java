// src/main/java/.../repository/UserComplimentLogRepository.java
package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.UserComplimentLog;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserComplimentLogRepository extends JpaRepository<UserComplimentLog, Long> {
    Optional<UserComplimentLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<UserComplimentLog> findByUserIdAndDateIn(Long userId, Collection<LocalDate> dates);
    Page<UserComplimentLog> findByUserIdAndIsArchivedTrueAndDateBetween(
            Long userId, LocalDate start, LocalDate end, Pageable pageable);
}
