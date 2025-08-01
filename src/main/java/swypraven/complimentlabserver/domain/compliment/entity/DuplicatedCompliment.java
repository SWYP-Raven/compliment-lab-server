package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swypraven.complimentlabserver.domain.user.entity.User;

@Entity
@Table(
        name = "duplicated_compliment",
        schema = "compliment_lab",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "compliment_id"})
        }
)
@Getter
@Setter
public class DuplicatedCompliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliment_id", nullable = false)
    private TodayCompliment compliment;
}