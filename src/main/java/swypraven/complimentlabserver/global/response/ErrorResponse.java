package swypraven.complimentlabserver.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;

    public static ApiResponse<?> from(ErrorCode errorCode) {
        return ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}