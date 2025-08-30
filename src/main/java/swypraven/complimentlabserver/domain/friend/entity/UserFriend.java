package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_friend")
public class UserFriend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne
    private Friend friend;


    private Boolean isDeleted = false;
}
