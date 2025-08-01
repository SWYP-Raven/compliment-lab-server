package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.DuplicatedCompliment;

public interface DuplicatedComplimentRepository extends JpaRepository<DuplicatedCompliment, Long> {
}
