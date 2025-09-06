package swypraven.complimentlabserver.domain.compliment.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
    @Builder
    public static class ChatCardArchiveItem {

        private final Long id;              // chat_compliment.id
        private final Long chatId;          // 원문 대화 ID
        private final String message;       // 카드에 저장된 문장
        private final String role;          // USER / ASSISTANT
        private final Long seed;            // seed (선택)
        private final String metaJson;      // 추가 메타(JSON 문자열)
        private final LocalDateTime createdAt; // 저장 시간
    }
}

