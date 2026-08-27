package swypraven.complimentlabserver.global.exception.compliment;

import swypraven.complimentlabserver.global.exception.common.DomainException;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

public class ComplimentException extends DomainException {
    public ComplimentException(ErrorCode code) { super(code); }

}
