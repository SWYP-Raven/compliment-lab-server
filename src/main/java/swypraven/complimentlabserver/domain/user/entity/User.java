package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import swypraven.complimentlabserver.domain.friend.entity.UserFriendType;
import swypraven.complimentlabserver.domain.user.model.request.UpdateUserRequest;

import java.util.ArrayList;
import java.util.List;

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
    private String nickname = "사용자";

    @Column(name = "today_alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean todayAlarm = false;

    @Column(name = "friend_alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean friendAlarm = false;

    @Column(name = "archive_alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean archiveAlarm = false;

    @Column(name = "marketing_alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean marketingAlarm = false;

    @Column(name = "event_alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean eventAlarm = false;


    @Column(name = "email", length = 191)
    private String email;

    @Column(name = "apple_sub", nullable = false, unique = true, length = 191)
    private String appleSub;

    @Column(name = "role", nullable = false, length = 50)
    private String role; // 예: ROLE_USER
    @Column(name = "seed", nullable = false)
    private Integer seed;

    // refresh token 저장용
    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserFriendType> friendTypes = new ArrayList<>();

    @PrePersist
    private void ensureSeed() {
        if (this.seed == null) {
            this.seed = (int) (Math.random() * 100_000);
        }
    }

    public Integer getSeed() {
        return this.seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    // 필요 시 편의 메서드들…

    public User setRole(String role) {
        this.role = role;
        return this;
    }


    public User update(UpdateUserRequest updateUserRequest) {
        this.nickname = updateUserRequest.nickname();
        this.friendAlarm = updateUserRequest.friendAlarm();
        this.archiveAlarm = updateUserRequest.archiveAlarm();
        this.marketingAlarm = updateUserRequest.marketingAlarm();
        this.eventAlarm = updateUserRequest.eventAlarm();

        return this;
    }
}
