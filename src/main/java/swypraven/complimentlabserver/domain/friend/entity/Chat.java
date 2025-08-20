package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.global.exception.chat.ChatCode;
import swypraven.complimentlabserver.global.exception.chat.ChatException;
import swypraven.complimentlabserver.global.exception.friend.FriendErrorCode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat", schema = "compliment_lab")
public class Chat {


    public Chat(RequestMessage requestMessage, Friend friend) {
        this.friend = friend;
        this.message = requestMessage.getMessage();

        switch (requestMessage.getRole()) {
            case "사용자":
                this.role = RoleType.USER;
                break;
            case "시스템":
                this.role = RoleType.SYSTEM;
                break;
            case "어시스턴트":
                this.role = RoleType.ASSISTANT;
                break;
            default:
                throw new ChatException(ChatCode.INVALID_ROLE);
        }
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

enum RoleType {
    USER,
    SYSTEM,
    ASSISTANT
}