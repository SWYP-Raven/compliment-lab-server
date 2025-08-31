package swypraven.complimentlabserver.domain.compliment.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class ArchiveRequests {

    @Getter @Setter
    public static class SaveTodayRequest {
        @NotNull
        private Long todayId;
    }

    @Getter @Setter
    public static class SaveChatCardRequest {
        @NotNull
        private Long chatId;

        // 선택: 카드 제목
        private String title;

        // 필수: 텍스트 본문
        @NotBlank
        private String content;

        // 선택: 렌더링 옵션(정렬/폰트/컬러 등)
        private Map<String, Object> meta;
    }
}
