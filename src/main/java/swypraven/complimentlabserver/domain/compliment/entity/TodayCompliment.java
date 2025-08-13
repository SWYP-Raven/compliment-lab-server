package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "today_compliment", schema = "compliment_lab")
public class TodayCompliment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TypeCompliment type;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private Instant createdAt;

}