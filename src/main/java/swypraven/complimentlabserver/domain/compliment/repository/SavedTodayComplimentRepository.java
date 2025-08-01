package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;

public interface SavedTodayComplimentRepository extends JpaRepository<SavedTodayCompliment, Long> {
}
