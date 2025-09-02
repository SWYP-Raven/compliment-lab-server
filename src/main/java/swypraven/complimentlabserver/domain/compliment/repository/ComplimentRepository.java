package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swypraven.complimentlabserver.domain.compliment.entity.Compliment;

import java.util.List;

public interface ComplimentRepository extends JpaRepository<Compliment, Integer> {

    // ID 오름차순: seed 셔플용
    @Query("select c.id from Compliment c order by c.id asc")
    List<Integer> findAllIdsSorted();


}
