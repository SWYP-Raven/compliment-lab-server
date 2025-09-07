package swypraven.complimentlabserver.domain.compliment.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 아카이브 DTO (seed 기반으로 수정됨)
 */
public class ArchiveDtos {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class TodayArchiveItem {
        private final Long id;              // saved_today_compliment.id
        private final String text;          // 칭찬 문장
        private final Long seed;            // 생성 seed
        private final LocalDateTime createdAt; // 저장 시간
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class ChatCardArchiveItem {

        private final Long id;              // chat_compliment.id

        @JsonProperty("chat_id")
        private final Long chatId;          // 원문 대화 ID

        @JsonProperty("type_id")
        private final String typeId;

        private final String message;       // 카드에 저장된 문장

        private final String role;          // USER / ASSISTANT

        @JsonProperty("meta_json")
        private final String metaJson;      // 추가 메타(JSON 문자열)

        @JsonProperty("created_at")
        private final LocalDateTime createdAt; // 저장 시간
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    public static class ChatCardArchiveItemList {
        @JsonProperty("chat_cards")
        private final List<ChatCardArchiveItem> chatCards;
    }
}
