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
@Table(name = "users", // ← 예약어 회피
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

    // 닉네임: 기본값 생성 전략을 쓰지 않는다면 nullable = true 권장
    @Column(name = "nickname", nullable = false)
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


    // 애플은 email이 없을 수 있음 → nullable = true
    @Column(name = "email", length = 191)
    private String email;

    // 애플 고유 사용자 ID(고정) - 유니크
    @Column(name = "apple_sub", nullable = false, unique = true, length = 191)
    private String appleSub;

    @Column(name = "role", nullable = false, length = 50)
    private String role; // 예: ROLE_USER

    // refresh token 저장용
    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

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
