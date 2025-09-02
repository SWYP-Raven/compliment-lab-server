// src/main/java/swypraven/complimentlabserver/domain/compliment/entity/UserComplimentLog.java
package swypraven.complimentlabserver.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_compliment_logs",
        indexes = {
                @Index(name = "idx_ucl_user_date", columnList = "user_id, date"),
                @Index(name = "idx_ucl_archived", columnList = "user_id, is_archived")
        },
        uniqueConstraints = {
                // 같은 유저+날짜에 1행만 존재 (upsert 로직 전제)
                @UniqueConstraint(name = "uk_ucl_user_date", columnNames = {"user_id", "date"})
        })
@Getter
@Setter
public class UserComplimentLog {
//isRead, isArchived 플래그를 저장하는 "상태 테이블"(유저별 칭찬 상태(읽음/보관)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 테이블을 FK로 묶을 거면 @ManyToOne로 바꿔도 됨 (지금은 단순 키 저장)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "compliment_id", nullable = false)
    private Integer complimentId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;
}
