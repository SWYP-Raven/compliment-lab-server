// UserCompliment.java (조인 엔티티)
package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swypraven.complimentlabserver.domain.user.entity.User;

@Entity
@Getter @Setter
@Table(name = "user_compliment")
public class UserCompliment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "compliment_id")
    private Compliment compliment;

    private boolean isRead;
    private boolean isArchived;
}
