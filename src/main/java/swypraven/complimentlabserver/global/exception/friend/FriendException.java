package swypraven.complimentlabserver.global.exception.friend;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class FriendException extends DomainException {
    public FriendException(FriendErrorCode errorCode) {
        super(errorCode);
    }
}
