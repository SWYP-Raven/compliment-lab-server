package swypraven.complimentlabserver.global.exception.chat;

import swypraven.complimentlabserver.global.exception.common.DomainException;

public class ChatException extends DomainException {
    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}
