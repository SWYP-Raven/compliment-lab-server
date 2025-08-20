package swypraven.complimentlabserver.global.auth.exception;

public class LoginFailedException extends RuntimeException {
    // Apple 로그인 실패
    public LoginFailedException() {super("애플 로그인에 실패했습니다.");}
    public LoginFailedException(String message) {super(message);}

    // 유효하지 않은 Refresh Token
    public static class InvalidJwtTokenException extends RuntimeException {
        public InvalidJwtTokenException(){super("유효하지 않은 토큰입니다.");}
        public InvalidJwtTokenException(String message) {super(message);}

        public InvalidJwtTokenException(String s, RuntimeException e) {
        }
    }

    // JWT 인증 관련 예외
    public class JwtAuthException extends RuntimeException {
        public JwtAuthException() { super("JWT 인증 실패"); }
        public JwtAuthException(String message) { super(message); }
    }


    // Apple ID 토큰 검증 실패
    public static class AppleIdTokenValidationException extends RuntimeException {
        public AppleIdTokenValidationException() { super("Apple ID Token 검증 실패"); }
        public AppleIdTokenValidationException(String message) { super(message); }
    }
}
