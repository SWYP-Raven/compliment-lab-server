package swypraven.complimentlabserver.domain.compliment.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

public class ArchiveDtos {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class TodayArchiveItem {
        private Long id;              // saved_today_compliment.id
        private Long todayId;         // today_compliment.id
        private Long typeId;          // type_compliment.id
        private String message;       // today_compliment.message
        private Instant createdAt;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ChatCardArchiveItem {
        private Long id;
        private Long chatId;
        private String title;            // ✅ 추가
        private String content;
        private Map<String,Object> meta;
        private String chatMessage;
        private Instant createdAt;
    }
}
