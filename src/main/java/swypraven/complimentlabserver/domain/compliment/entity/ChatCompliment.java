package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.user.entity.User;

import java.time.LocalDateTime;

// 카드 아카이브: 이미지 기반
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
public class ChatCompliment {

    public ChatCompliment(User user, Chat chat) {
        this.user = user;
        this.chat = chat;
    }
  
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

    /** 공개 URL (S3 등) */
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    /** 썸네일(옵션) */
    @Column(name = "thumb_url", length = 512)
    private String thumbUrl;

    /** 렌더링 옵션(폰트/컬러/캔버스 크기 등) */
    @JdbcTypeCode(SqlTypes.JSON) // Hibernate 6 + MySQL 8 JSON 컬럼
    @Column(name = "payload", columnDefinition = "json")
    private Map<String, Object> payload;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
  
    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    // 원하면 빌더 대신 사용 가능한 팩토리 메서드
    public static ChatCompliment of(User user, Chat chat, String imageUrl, String thumbUrl, Map<String, Object> payload) {
        return ChatCompliment.builder()
                .user(user)
                .chat(chat)
                .imageUrl(imageUrl)
                .thumbUrl(thumbUrl)
                .payload(payload)
                .build();
    }
}
