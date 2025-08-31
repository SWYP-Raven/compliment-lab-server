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
        private final Long id;           // saved_today_compliment.id
        private final Long todayId;      // today_compliment.id
        private final Long typeId;       // today_compliment.type_id
        private final String message;    // today_compliment.message
        private final Instant createdAt; // saved_today_compliment.created_at
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ChatCardArchiveItem {
        private final Long id;                 // chat_compliment.id
        private final Long chatId;             // chat.id (원문 대화)
        private final String title;            // 대화 문장 카드 제목
        private final String content;          // 카드 본문 텍스트(필수)
        private final Map<String, Object> meta;// 렌더 옵션(JSON)
        private final String chatMessage;      // 원문 대화 내용(chat.message)
        private final Instant createdAt;       // chat_compliment.created_at
    }
}
