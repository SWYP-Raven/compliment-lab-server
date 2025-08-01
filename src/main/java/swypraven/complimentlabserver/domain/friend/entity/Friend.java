package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "friend", schema = "compliment_lab")
public class Friend {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeCompliment type;

    @Column(name = "name")
    private String name;

}