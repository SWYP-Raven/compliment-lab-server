package swypraven.complimentlabserver.global.exception.user;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class UserException extends DomainException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }
}
