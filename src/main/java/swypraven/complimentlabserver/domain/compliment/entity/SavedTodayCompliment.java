package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Entity
@Table(
        name = "saved_today_compliment",
        schema = "compliment_lab",
        uniqueConstraints = {
                @UniqueConstraint(name="ux_saved_today_user_today", columnNames = {"user_id","today_id"})
        },
        indexes = {
                @Index(name="ix_saved_today_user_created", columnList = "user_id, created_at")
        }
)
public class SavedTodayCompliment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "today_id", nullable = false)
    private TodayCompliment todayCompliment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
