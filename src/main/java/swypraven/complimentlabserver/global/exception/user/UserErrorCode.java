package swypraven.complimentlabserver.global.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("USER_01", "해당 유저는 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
