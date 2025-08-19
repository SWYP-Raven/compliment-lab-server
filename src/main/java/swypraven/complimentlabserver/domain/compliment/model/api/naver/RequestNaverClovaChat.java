package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestNaverClovaChat {
    private List<Message>  messages;
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