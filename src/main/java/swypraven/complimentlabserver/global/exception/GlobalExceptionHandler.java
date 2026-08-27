package swypraven.complimentlabserver.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import swypraven.complimentlabserver.global.exception.auth.AuthException;
import swypraven.complimentlabserver.global.exception.common.DomainException;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;
import swypraven.complimentlabserver.global.exception.user.UserException;
import swypraven.complimentlabserver.global.response.ApiResponse;
import swypraven.complimentlabserver.global.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 도메인 공통: ErrorCode 기반으로 상태/메시지 내려줌 */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomainException(DomainException ex) {
        ErrorCode code = ex.getErrorCode();
        log.info("[Domain] {} : {}", code.getCode(), code.getMessage());
        return ResponseEntity.status(code.getHttpStatus()).body(ErrorResponse.from(code));
    }

    /** 도메인 세부분류 (필요 시 유지). 더 구체적이므로 위 핸들러보다 우선 매칭됨 */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> handleUserException(UserException ex) {
        log.info("[User] {} : {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ErrorResponse.from(ex.getErrorCode()));
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<?> handleFriendException(FriendException ex) {
        log.info("[Friend] {} : {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ErrorResponse.from(ex.getErrorCode()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuth(AuthException e) {
        log.info("[Auth] {} : {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    /* ==== 흔한 4xx 바인딩/요청 에러들 ==== */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        // 필요하면 ErrorResponse에 fieldErrors를 추가해서 내려주세요.
        log.info("[400] Validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.simple("VALIDATION_FAILED", "요청 값 검증에 실패했습니다."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
        log.info("[400] Missing parameter: {}", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.simple("MISSING_PARAMETER", "필수 파라미터가 누락되었습니다: " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.info("[400] Type mismatch: {} -> {}", ex.getName(), ex.getRequiredType());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.simple("TYPE_MISMATCH", "파라미터 타입이 올바르지 않습니다."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(HttpMessageNotReadableException ex) {
        log.info("[400] Unreadable body: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.simple("INVALID_JSON", "요청 본문을 읽을 수 없습니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.info("[405] Method not supported: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.simple("METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        log.info("[403] Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.simple("FORBIDDEN", "접근 권한이 없습니다."));
    }

    /* ==== 최종 캐치올 ==== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("[500] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.simple("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }
}
