package swypraven.complimentlabserver.global.exception.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    INVALID_ROLE("COMPLIMENT_TYPE_1", "해당 유형은 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("COMPLIMENT_CHAT_NOT_FOUND", "해당하는 메시지는 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_SAVE_ROLE_TYPE("COMPLIMENT_RORLE_TYPE", "ASISSTANT만 저장할 수 있습니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
