package swypraven.complimentlabserver.domain.compliment.model.dto.naver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class AIFilter {
    private String groupName;
    private String name;
    private String score;
    private String result;

}
