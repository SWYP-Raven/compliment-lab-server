package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "saved_today_compliment",
        schema = "compliment_lab",
        // 필요 시 중복 방지용 유니크 제약(텍스트+씨드 조합) — seed가 null일 수도 있으면 제거하세요.
        uniqueConstraints = {
                // @UniqueConstraint(name = "ux_today_text_seed_user", columnNames = {"user_id", "text", "seed"})
        },
        indexes = {
                @Index(name = "ix_saved_today_user_created", columnList = "user_id, created_at")
        }
)
public class SavedTodayCompliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** 소유 유저 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 저장된 칭찬 문장 */
    @Column(name = "text", nullable = false, columnDefinition = "text")
    private String text;

    /** 생성 파라미터(선택) */
    @Column(name = "seed")
    private Long seed;

    /** 생성 시각(UTC) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
