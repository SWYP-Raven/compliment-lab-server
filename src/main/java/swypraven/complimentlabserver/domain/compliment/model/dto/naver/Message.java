package swypraven.complimentlabserver.domain.compliment.model.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
class Message {
    private final List<Content> content;
    private final String role;
}
