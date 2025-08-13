package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "saved_today_compliment", schema = "compliment_lab")
public class SavedTodayCompliment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "today_id", nullable = false)
    private TodayCompliment todayCompliment;

    @Column(name = "created_at")
    private Instant createdAt;

}