package swypraven.complimentlabserver.domain.compliment.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ChatCardArchiveItem {
        private Long id;              // chat_compliment.id
        private Long chatId;          // chat.id
        private String imageUrl;
        private String thumbUrl;      // nullable
        private Map<String, Object> payload; // nullable
        private String chatMessage;   // 미리보기용
        private LocalDateTime createdAt;
    }
}
