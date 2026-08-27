package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.util.List;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {

    @Query("SELECT t FROM TodayCompliment t ORDER BY t.id ASC")
    List<TodayCompliment> findPage(Pageable pageable);
}
