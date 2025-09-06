package swypraven.complimentlabserver.global.exception.archive;

import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

public enum ArchiveErrorCode implements ErrorCode {

    DUPLICATE_TODAY(HttpStatus.CONFLICT, "이미 저장된 오늘의 칭찬입니다."),
    TODAY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 오늘의 칭찬입니다."),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 대화입니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."),
    CARD_NOT_FOUND_OR_FORBIDDEN(HttpStatus.FORBIDDEN, "삭제할 카드가 없거나 권한이 없습니다."),
    TODAY_NOT_FOUND_OR_FORBIDDEN(HttpStatus.FORBIDDEN, "삭제할 항목이 없거나 권한이 없습니다."),
    TEXT_EMPTY(HttpStatus.BAD_REQUEST, "오늘의 칭찬 내용(text)은 비어 있을 수 없습니다."),
    MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "메세지(message)는 비어 있을 수 없습니다." ),
    ROLE_EMPTY(HttpStatus.BAD_REQUEST, "역할(role)은 비어 있을 수 없습니다."  ),
    CHAT_NOT_FOUND_OR_FORBIDDEN(HttpStatus.FORBIDDEN, "이 대화에 대한 아카이브 권한이 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ArchiveErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        // 보통 에러코드는 enum name()을 그대로 쓰면 충분합니다.
        return name();
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
