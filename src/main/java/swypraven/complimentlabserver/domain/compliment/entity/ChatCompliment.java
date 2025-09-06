package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
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

    /** 카드가 만들어진 원본 대화 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    /** 소유 유저 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** 저장할 문장 (필수) */
    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    /** 원문 역할 (USER | ASSISTANT 등) */
    @Column(name = "role", length = 20, nullable = false)
    private String role;

    /** 생성 파라미터들(선택) */
    @Column(name = "seed")
    private Long seed;

    /** 추가 옵션(JSON 문자열) */
    @Column(name = "meta_json", columnDefinition = "json")
    private String metaJson;

    /** 생성 시각(UTC) */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    /** 팩토리 메서드 (seed 기반) */
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
