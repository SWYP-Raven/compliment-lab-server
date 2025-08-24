package swypraven.complimentlabserver.global.exception.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum ChatCode implements ErrorCode {

    INVALID_ROLE("COMPLIMENT_TYPE_1", "해당 유형은 없습니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
