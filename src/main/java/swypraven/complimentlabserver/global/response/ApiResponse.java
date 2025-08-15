package swypraven.complimentlabserver.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swypraven.complimentlabserver.global.exception.common.DomainException;

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
}