package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "chat", schema = "compliment_lab")
public class Chat {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private Instant createdAt;

}