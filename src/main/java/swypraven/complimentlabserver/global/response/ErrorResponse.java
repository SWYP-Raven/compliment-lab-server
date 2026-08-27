package swypraven.complimentlabserver.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;

    /** ErrorCode 기반 생성 */
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    /** 간단히 코드 + 메시지로 직접 생성 */
    public static ErrorResponse simple(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
