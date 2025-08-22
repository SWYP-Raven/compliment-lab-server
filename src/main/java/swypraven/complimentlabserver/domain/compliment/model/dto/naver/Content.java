package swypraven.complimentlabserver.domain.compliment.model.dto.naver;

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
