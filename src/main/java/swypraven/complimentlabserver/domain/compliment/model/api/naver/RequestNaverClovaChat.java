package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class RequestNaverClovaChat {

    public RequestNaverClovaChat(String role, String prompt, RequestMessage requestMessage) {
        List<Message> requestMessages = new ArrayList<>();
        Message message = new Message(List.of(new Content("text", prompt)), role);
        requestMessages.add(message);

        this.messages = requestMessages;
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
@ToString
@AllArgsConstructor
class Message {
    private List<Content> content;
    private String role;
}

@Getter
@ToString
@AllArgsConstructor
class Content {
    private String type;
    private String text;
}