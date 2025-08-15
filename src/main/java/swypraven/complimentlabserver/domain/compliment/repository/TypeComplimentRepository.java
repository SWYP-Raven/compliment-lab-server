package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;

import java.util.Optional;

public interface TypeComplimentRepository extends JpaRepository<TypeCompliment, Long> {
    Optional<TypeCompliment> findByName(String name);
}
