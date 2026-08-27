package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swypraven.complimentlabserver.domain.compliment.entity.TypeCompliment;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_friend_type")
public class UserFriendType {

    public UserFriendType(User user, TypeCompliment typeCompliment) {
        this.user = user;
        this.typeCompliment = typeCompliment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL) // 모든 cascade 적용
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private TypeCompliment typeCompliment;
}
