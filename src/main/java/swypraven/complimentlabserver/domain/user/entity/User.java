package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean alarm = false;

    @Column(name = "email", nullable = false)
    private String email;

}