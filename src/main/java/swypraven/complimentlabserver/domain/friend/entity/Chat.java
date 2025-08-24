package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat", schema = "compliment_lab")
public class Chat {
  
    public Chat(String chat, RoleType role, Friend friend) {
        this.friend = friend;
        this.message = chat;
        this.role = role;
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private Friend friend;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Column(name = "role")
    private RoleType role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

