package swypraven.complimentlabserver.domain.chat.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
class Content {
    private final String type;
    private final String text;
}
