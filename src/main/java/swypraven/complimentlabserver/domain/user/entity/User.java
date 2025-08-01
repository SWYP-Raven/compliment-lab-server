package swypraven.complimentlabserver.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "alarm", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean alarm = false;

    @Column(name = "mail", nullable = false)
    private String mail;

}