package swypraven.complimentlabserver.domain.compliment.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResponseMessage {
    String message;
    LocalDateTime time;
}
