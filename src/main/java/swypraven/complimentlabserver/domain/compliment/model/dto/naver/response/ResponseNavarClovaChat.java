package swypraven.complimentlabserver.domain.compliment.model.dto.naver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseNavarClovaChat {
    private Message.Status status;
    private Result result;

    public String getMessage() {
        return result.getMessage().getContent();
    }
}

