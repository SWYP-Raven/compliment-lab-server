package swypraven.complimentlabserver.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import swypraven.complimentlabserver.global.exception.common.DomainException;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;
import swypraven.complimentlabserver.global.response.ApiResponse;
import swypraven.complimentlabserver.global.response.ErrorResponse;

@Slf4j
@ControllerAdvice
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.warn(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(ex.getMessage());
    }
}
