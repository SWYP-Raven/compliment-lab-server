package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.entity.UserFriendType;
import swypraven.complimentlabserver.domain.user.model.request.UpdateUserRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_apple_sub", columnNames = {"apple_sub"})
        }
)
public class User {

    public User(String email, String appleSub) {
        this.email = email;
        this.appleSub = appleSub;
        this.nickname = "사용자"; // 기본 닉네임 보장
        this.role = "ROLE_USER"; // 기본 권한 설정
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserFriendType> friendTypes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedTodayCompliment> savedTodayCompliments = new ArrayList<>();


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

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    public User update(UpdateUserRequest updateUserRequest) {
        this.nickname = updateUserRequest.safeNickname();
        this.friendAlarm = updateUserRequest.safeFriendAlarm();
        this.archiveAlarm = updateUserRequest.safeArchiveAlarm();
        this.marketingAlarm = updateUserRequest.safeMarketingAlarm();
        this.eventAlarm = updateUserRequest.safeEventAlarm();
        return this;
    }
}
