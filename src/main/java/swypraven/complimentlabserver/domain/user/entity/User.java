package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import swypraven.complimentlabserver.domain.user.model.request.UpdateUserRequest;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_apple_sub", columnNames = {"apple_sub"})
        })
public class User {

    public User(String email, String appleSub) {
        this.email = email;
        this.appleSub = appleSub;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean alarm = false;

    @Column(name = "email", length = 191)
    private String email;

    @Column(name = "apple_sub", nullable = false, unique = true, length = 191)
    private String appleSub;

    @Column(name = "role", nullable = false, length = 50)
    private String role; // 예: ROLE_USER

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "seed", nullable = false)
    private Integer seed;

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public Integer getSeed() {
        return this.seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    // 선택: seed 보장
    @PrePersist
    private void ensureSeed() {
        if (this.seed == null) {
            this.seed = (int) (Math.random() * 100_000);
        }
    }

    /** 단일 alarm만 보유하므로 friendAlarm을 대표로 매핑 */
    public User update(UpdateUserRequest request) {
        if (request.nickname() != null) {
            this.nickname = request.nickname();
        }
        if (request.friendAlarm() != null) {
            this.alarm = request.friendAlarm();
        }
        // 아래 3개는 현재 엔티티에 컬럼이 없으므로 무시(확장 시 추가)
        // if (request.archiveAlarm() != null) { ... }
        // if (request.marketingAlarm() != null) { ... }
        // if (request.eventAlarm() != null) { ... }
        return this;
    }
}
