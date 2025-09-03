package swypraven.complimentlabserver.domain.compliment.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/** 특정 날짜의 칭찬 + 읽음/아카이브 상태 DTO */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayDto {

    @Schema(description = "해당 날짜", example = "2025-09-01")
    private final LocalDate date;

    @Schema(description = "칭찬 ID", example = "37")
    private final Long id;

    @Schema(description = "칭찬 문구", example = "오늘도 꾸준히 해내고 있네요!")
    private final String message;

    @Schema(description = "칭찬 타입", example = "과정")
    private final String type;

    @JsonProperty("is_read")
    @Schema(description = "열람 여부", example = "true")
    private final boolean isRead;

    @JsonProperty("is_archived")
    @Schema(description = "아카이브 여부", example = "false")
    private final boolean isArchived;

    public static TodayDto of(LocalDate date,
                              Long id,
                              String message,
                              String type,
                              boolean isRead,
                              boolean isArchived) {
        return TodayDto.builder()
                .date(date)
                .id(id)
                .message(message)
                .type(type)
                .isRead(isRead)
                .isArchived(isArchived)
                .build();
    }
}
