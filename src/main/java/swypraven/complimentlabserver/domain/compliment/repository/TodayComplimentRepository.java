package swypraven.complimentlabserver.domain.compliment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodayComplimentRepository extends JpaRepository<TodayCompliment, Long> {
    // KST 오늘 0시~내일 0시 범위에서 최신 1건
// 전체 개수는 JpaRepository.count() 사용
    // 아래 메서드로 고정 정렬 + 오프셋 1건만 뽑아옵니다.
    @Query("SELECT t FROM TodayCompliment t ORDER BY t.id ASC")
    List<TodayCompliment> findPage(Pageable pageable);

}