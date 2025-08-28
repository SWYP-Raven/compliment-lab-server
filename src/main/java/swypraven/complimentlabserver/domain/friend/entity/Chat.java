package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import java.time.LocalDateTime;

@Getter 
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor 
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(
        name = "chat",
        schema = "compliment_lab",
        indexes = {
                @Index(name = "ix_chat_friend_created", columnList = "friend_id, created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Chat {
  
    public Chat(String chat, RoleType role, Friend friend) {
        this.friend = friend;
        this.message = chat;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;


    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (role == null) role = ChatRole.user;
    }
}
    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Column(name = "role")
    private RoleType role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}