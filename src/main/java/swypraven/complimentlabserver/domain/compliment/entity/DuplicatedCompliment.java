package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.user.entity.User;
//이미 받은 칭찬
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@Entity
@Table(
        name = "duplicated_compliment",
        schema = "compliment_lab",
        uniqueConstraints = {
                @UniqueConstraint(name="ux_dup_user_compliment", columnNames = {"user_id", "compliment_id"})
        },
        indexes = {
                @Index(name="ix_dup_user_read", columnList = "user_id, is_read")
        }
)
public class DuplicatedCompliment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "compliment_id", nullable = false)
    private TodayCompliment compliment;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @PrePersist
    void prePersist() {
        if (isRead == null) isRead = false;
    }
}
