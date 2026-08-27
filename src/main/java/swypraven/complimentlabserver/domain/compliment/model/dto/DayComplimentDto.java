package swypraven.complimentlabserver.domain.compliment.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record DayComplimentDto(
        @Schema(description = "해당 날짜", example = "2025-09-01")
        LocalDate date,

        @Schema(description = "해당 날짜의 칭찬 정보")
        ComplimentDto compliment,

        @JsonProperty("is_read")
        @Schema(description = "열람 여부", example = "true")
        boolean isRead,

        @JsonProperty("is_archived")
        @Schema(description = "아카이브 여부", example = "false")
        boolean isArchived
) {}
