package swypraven.complimentlabserver.global.exception.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    // Apple 인증 관련
    APPLE_TOKEN_INVALID("AUTH_001", "Apple ID 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_TOKEN_EXPIRED("AUTH_002", "Apple ID 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    APPLE_PUBLIC_KEY_LOAD_FAILED("AUTH_003", "Apple 공개키 로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_AUTH_FAILED("AUTH_004", "Apple 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),

    // JWT 관련
    JWT_TOKEN_INVALID("AUTH_005", "유효하지 않은 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_EXPIRED("AUTH_006", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_UNSUPPORTED("AUTH_007", "지원되지 않는 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_TOKEN_EMPTY("AUTH_008", "JWT 토큰이 비어있습니다.", HttpStatus.BAD_REQUEST),
    JWT_SIGNATURE_INVALID("AUTH_009", "JWT 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    JWT_MALFORMED("AUTH_010", "잘못된 형식의 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),

    // Refresh Token 관련
    REFRESH_TOKEN_NOT_FOUND("AUTH_011", "존재하지 않는 Refresh Token입니다.", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_INVALID("AUTH_012", "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("AUTH_013", "Refresh Token이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    // 범용(외부 라이브러리 예외 매핑 등)
    TOKEN_INVALID("AUTH_014", "토큰 파싱/검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("AUTH_015", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR("AUTH_999", "내부 인증 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    EXIST_USER("AUTH_016", "이미 가입된 회원입니다.", HttpStatus.BAD_REQUEST),
    NONE_EXIST_USER("AUTH_17", "가입되지 않은 회원입니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
