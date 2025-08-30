package swypraven.complimentlabserver.domain.compliment.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class ArchiveRequests {

    @Getter @Setter
    public static class SaveTodayRequest {
        @jakarta.validation.constraints.NotNull
        private Long todayId;
    }

    @Getter @Setter
    public static class SaveChatCardRequest {
        @NotNull
        private Long chatId;
        @NotNull
        private String imageUrl;
        private String thumbUrl;                  // optional
        private Map<String, Object> payload;     // optional
    }
}
