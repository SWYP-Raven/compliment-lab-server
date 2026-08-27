package swypraven.complimentlabserver.domain.compliment.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 아카이브 관련 요청 DTO
 * - 오늘의 칭찬 (seed 기반)
 * - 대화 카드 (seed 기반)
 */
public class ArchiveRequests {

    /**
     * 오늘의 칭찬 저장 요청
     * seed 기반으로 이미 생성된 문장을 보존
     */
    @Getter @Setter
    public static class SaveTodayBySeedRequest {
        @NotBlank
        private String text;      // 생성된 칭찬 문장 (필수)

        @NotNull
        private Long seed;        // 재현성을 위한 seed (필수)
    }

    /**
     * 대화 카드 저장 요청
     * 대화 중 마음에 드는 문장을 보존 (seed 메타 포함 가능)
     */
    @Getter @Setter
    public static class SaveChatCardBySeedRequest {
        @NotNull
        private Long chatId;      // 어떤 대화의 문장인지 (필수)

        @NotBlank
        private String message;   // 보존할 문장 텍스트 (필수)

        @NotBlank
        private String role;      // 작성자 역할 ("USER" / "ASSISTANT") (필수)

        private Long seed;        // 생성된 문장이라면 seed (선택)

        @Deprecated
        private String metaJson;  // 추가 메타데이터(JSON 문자열, 선택)
    }
}
