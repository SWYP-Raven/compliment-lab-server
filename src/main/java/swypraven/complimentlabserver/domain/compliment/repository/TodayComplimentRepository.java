package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {
}
