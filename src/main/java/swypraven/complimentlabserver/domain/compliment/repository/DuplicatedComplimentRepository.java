package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swypraven.complimentlabserver.domain.compliment.entity.DuplicatedCompliment;

@Repository
public interface DuplicatedComplimentRepository extends JpaRepository<DuplicatedCompliment, Long> {

    boolean existsByUserIdAndComplimentId(Long userId, Long complimentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update DuplicatedCompliment d set d.isRead = true where d.user.id = :userId and d.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
}
