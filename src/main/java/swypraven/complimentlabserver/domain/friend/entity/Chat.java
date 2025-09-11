package swypraven.complimentlabserver.domain.friend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    //칭구 삭제 부분 관련 추가
    @OneToMany(mappedBy = "chat",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ChatCompliment> compliments = new ArrayList<>();

    @Lob
    @Column(name = "message", nullable = false, columnDefinition = "LONGTEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (role == null) role = RoleType.USER; // 프로젝트 enum 값에 맞춰 조정
    }
}
