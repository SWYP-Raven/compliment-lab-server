package swypraven.complimentlabserver.global.exception.auth;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class AuthException extends DomainException {
    private static final String message = "";

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(errorCode);
    }
}
