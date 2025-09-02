package swypraven.complimentlabserver.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.DomainException;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data, String code, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String code, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .build();
    }


    public static <T> ApiResponse<T> error(DomainException ex) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
    }

    public static <T> ApiResponse<T> error(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponse.<T>builder()
                .success(false)
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message("서버에 예기치 못한 오류가 발생 하였습니다.")
                .build();
    }

    public static <T> ApiResponse<T> of(boolean success, String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(success)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> of(boolean success, T data, String message) {
        return ApiResponse.<T>builder()
                .success(success)
                .code(success ? null : "ERROR") // ★ 성공이면 null, 실패면 "ERROR"
                .message(message)
                .data(data)
                .build();
    }

}