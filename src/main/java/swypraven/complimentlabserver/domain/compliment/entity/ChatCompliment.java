package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "chat_compliment", schema = "compliment_lab")
public class ChatCompliment {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private Instant createdAt;

}