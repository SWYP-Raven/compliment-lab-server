package swypraven.complimentlabserver.domain.friend.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Getter
@RequiredArgsConstructor
public class LastMessageDto {

    private final String message;
    private final LocalDateTime time;

}
