package swypraven.complimentlabserver.global.exception.auth;

public class LoginFailedException extends RuntimeException {
    // Apple 로그인 실패 (최상위)
    public LoginFailedException() { super("애플 로그인에 실패했습니다."); }
    public LoginFailedException(String message) { super(message); }
    public LoginFailedException(String message, Throwable cause) { super(message, cause); }

    // 유효하지 않은 Refresh Token
    public static class InvalidJwtTokenException extends RuntimeException {
        public InvalidJwtTokenException() { super("유효하지 않은 토큰입니다."); }
        public InvalidJwtTokenException(String message) { super(message); }
        public InvalidJwtTokenException(String message, Throwable cause) { super(message, cause); }
    }

    // JWT 인증 관련 예외
    public static class JwtAuthException extends RuntimeException {
        public JwtAuthException() { super("JWT 인증 실패"); }
        public JwtAuthException(String message) { super(message); }
        public JwtAuthException(String message, Throwable cause) { super(message, cause); }
    }

    // Apple ID 토큰 검증 실패
    public static class AppleIdTokenValidationException extends RuntimeException {
        public AppleIdTokenValidationException() { super("Apple ID Token 검증 실패"); }
        public AppleIdTokenValidationException(String message) { super(message); }
        public AppleIdTokenValidationException(String message, Throwable cause) { super(message, cause); }
    }
}
