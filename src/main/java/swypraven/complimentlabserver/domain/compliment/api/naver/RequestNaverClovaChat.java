package swypraven.complimentlabserver.domain.compliment.api.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class RequestNaverClovaChat {

    public RequestNaverClovaChat(String prompt, List<Chat> history,  RequestMessage requestMessage) {

    }

    private List<Message> messages;
    private double topP = 0.8;
    private int maxTokens = 256;
    private double temperature = 0.8;
    private List<String> stopBefore = new ArrayList<>();
    private int seed = 0;
    private boolean includeAiFilters  = true;
}


@Getter
@AllArgsConstructor
class Message {
    private List<Content> content;
    private String role;
}

@Getter
@AllArgsConstructor
class Content {
    private String type;
    private String text;
}