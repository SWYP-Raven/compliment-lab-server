package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestNaverClovaChat {

    public RequestNaverClovaChat() {

    }

    private List<Message> messages;
    private String topP;
    private String topK;
    private int maxTokens;
    private double temperature;
    private List<String> stopBefore;
    private int seed;
    private boolean includeAiFilters;
}

@Getter
@AllArgsConstructor
class Message {
    private String message;
    private String role;
}