package swypraven.complimentlabserver.global.auth;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class AuthException extends DomainException {
    private static final String message = "";

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode, message);
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
