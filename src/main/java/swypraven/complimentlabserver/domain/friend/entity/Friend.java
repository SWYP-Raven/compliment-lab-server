package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "friend", schema = "compliment_lab")
public class Friend {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeCompliment type;

    @Column(name = "name")
    private String name;

    @Builder
    public Friend(User user, TypeCompliment type, String name) {
        this.user = user;
        this.type = type;
        this.name = name;
    }

}