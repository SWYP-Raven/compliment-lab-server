package swypraven.complimentlabserver.global.exception;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jwt.proc.BadJWTException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;
import swypraven.complimentlabserver.global.exception.common.DomainException;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;
import swypraven.complimentlabserver.global.exception.user.UserException;
import swypraven.complimentlabserver.global.response.ApiResponse;
import swypraven.complimentlabserver.global.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomainException(DomainException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(ex.getHttpStatus()).body(ErrorResponse.from(code));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException ex) {
        log.info("{} : {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex));
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<?> handleFriendException(FriendException ex) {
        log.info("{} : {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ApiResponse.error(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(ex.getMessage());
    }
    // 3) 우리가 의도적으로 던진 로그인/토큰 도메인 예외
    @ExceptionHandler(LoginFailedException.AppleIdTokenValidationException.class)
    public ResponseEntity<?> handleAppleRule(LoginFailedException.AppleIdTokenValidationException e) {
        log.info("[APPLE] rule violation : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(AuthErrorCode.APPLE_TOKEN_INVALID));
    }

    @ExceptionHandler(LoginFailedException.InvalidJwtTokenException.class)
    public ResponseEntity<?> handleInvalidJwt(LoginFailedException.InvalidJwtTokenException e) {
        log.info("[JWT] invalid token : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(AuthErrorCode.TOKEN_INVALID));
    }

    @ExceptionHandler(LoginFailedException.JwtAuthException.class)
    public ResponseEntity<?> handleJwtAuth(LoginFailedException.JwtAuthException e) {
        log.info("[JWT] auth failed : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(AuthErrorCode.UNAUTHORIZED));
    }

    // 4) Nimbus/JWT 파싱·서명 등 외부 예외 → 안전한 코드로 통일
    @ExceptionHandler({ BadJWTException.class, BadJWSException.class, JOSEException.class, ParseException.class })
    public ResponseEntity<?> handleJoseAndParse(Exception e) {
        log.warn("[JWT/NIMBUS] {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.from(AuthErrorCode.TOKEN_INVALID)); // 메시지는 마스킹
    }
}
