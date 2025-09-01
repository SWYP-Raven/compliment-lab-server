package swypraven.complimentlabserver.domain.compliment.model.response;

import lombok.Builder;
import lombok.Getter;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.LocalDateTime;

@Getter
@Builder
public class TodayDto {
    private final Long id;
    private final Long typeId;
    private final String message;
    private final LocalDateTime createdAt;

    public static TodayDto from(TodayCompliment tc) {
        return TodayDto.builder()
                .id(tc.getId())
                .typeId(tc.getType() != null ? tc.getType().getId() : null)
                .message(tc.getMessage())            // <-- text -> message
                .createdAt(tc.getCreatedAt())        // <-- date -> createdAt(Instant)
                .build();
    }
}
