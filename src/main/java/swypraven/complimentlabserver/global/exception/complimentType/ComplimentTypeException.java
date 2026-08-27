package swypraven.complimentlabserver.global.exception.complimentType;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class ComplimentTypeException extends DomainException {
    public ComplimentTypeException(ComplimentTypeCode errorCode) {
        super(errorCode);
    }
}
