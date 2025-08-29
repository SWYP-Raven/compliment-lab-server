package swypraven.complimentlabserver.domain.compliment.model.dto.naver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
class Result {
    private Message message;
    private String finishReason;
    private long created;
    private long seed;
    private Usage usage;
    private List<AIFilter> aiFilter;
}
