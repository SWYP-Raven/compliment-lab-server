package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.chat.entity.Chat;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "chat_compliment",
        schema = "compliment_lab",
        indexes = {
                @Index(name = "ix_chat_comp_user_created", columnList = "user_id, created_at")
        }
)
public class ChatCompliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "role", length = 20, nullable = false)
    private String role;

    @Column(name = "seed")
    private Long seed;

    @Column(name = "meta_json", columnDefinition = "json")
    private String metaJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public static ChatCompliment of(
            User user,
            Chat chat,
            String message,
            String role,
            Long seed,
            String metaJson
    ) {
        return ChatCompliment.builder()
                .user(user)
                .chat(chat)
                .message(message)
                .role(role)
                .seed(seed)
                .metaJson(metaJson)
                .build();
    }
}
