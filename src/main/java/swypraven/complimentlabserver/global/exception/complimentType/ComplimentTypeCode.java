package swypraven.complimentlabserver.global.exception.complimentType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum ComplimentTypeCode implements ErrorCode {

    NOT_FOUND("COMPLIMENT_TYPE_1", "해당 유형은 없습니다.", HttpStatus.NOT_FOUND),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
