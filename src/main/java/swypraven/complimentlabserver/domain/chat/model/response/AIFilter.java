package swypraven.complimentlabserver.domain.chat.model.response;

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
