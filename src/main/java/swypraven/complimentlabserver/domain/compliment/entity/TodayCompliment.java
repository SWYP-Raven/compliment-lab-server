package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@Entity
@Table(name = "today_compliment", schema = "compliment_lab")
public class TodayCompliment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeCompliment type;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    // ✅ 헬퍼: 기존 코드 호환용
    public String getText() {
        return message;
    }

    // ✅ KST 기준 '날짜'를 반환 (엔티티에 굳이 둘 필요는 없지만, 이미 호출부가 있으면 이렇게)
    public LocalDate getTargetDate() {
        if (createdAt == null) return null;
        return createdAt.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
    }
}
