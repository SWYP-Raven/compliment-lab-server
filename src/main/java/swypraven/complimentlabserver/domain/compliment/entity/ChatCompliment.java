package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.Map;

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
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** 카드 제목(선택) */
    @Column(name = "title", length = 200)
    private String title;

    /** 카드 본문 텍스트(필수) */
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    /** 렌더링 옵션(폰트/컬러/정렬 등) */
    @JdbcTypeCode(SqlTypes.JSON) // Hibernate 6 + MySQL 8 JSON 컬럼
    @Column(name = "meta_json", columnDefinition = "json")
    private Map<String, Object> meta;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    /** 팩토리 메서드 (텍스트 중심) */
    public static ChatCompliment of(User user, Chat chat, String title, String content, Map<String, Object> meta) {
        return ChatCompliment.builder()
                .user(user)
                .chat(chat)
                .title(title)
                .content(content)
                .meta(meta)
                .build();
    }
}
