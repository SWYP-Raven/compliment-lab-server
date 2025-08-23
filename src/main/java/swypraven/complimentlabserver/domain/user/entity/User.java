package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Index;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean alarm = false;

    @Column(name = "email", nullable = false, length = 191)
    private String email;

    @Column(nullable = false, unique = true, length = 191)
    private String appleSub; // 애플 고유 사용자 ID(고정)

    @Column(nullable = false, length = 50)
    private String role;     // 예: ROLE_USER


    // refresh token 저장용
    @Column(length = 512)
    private String refreshToken;

    public void setAppleSub(String appleSub) {
        this.appleSub = appleSub;
    }

    public void setRole(String roleUser) {
        this.role = roleUser;
    }

}
