package swypraven.complimentlabserver.global.exception.archive;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class ArchiveException extends DomainException {
    public ArchiveException(ArchiveErrorCode code) { super(code); }
}
