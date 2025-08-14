package swypraven.complimentlabserver.global.exception.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum FriendErrorCode implements ErrorCode {

    EXIST_FRIEND("FRIEND_1", "이미 존재하는 친구입니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
