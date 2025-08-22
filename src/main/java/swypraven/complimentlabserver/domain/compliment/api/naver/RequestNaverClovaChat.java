package swypraven.complimentlabserver.domain.compliment.api.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class RequestNaverClovaChat {

    public RequestNaverClovaChat(String prompt, List<Chat> history,  RequestMessage requestMessage) {
        this.messages = new ArrayList<>();

        // 1. 시스템 프롬프트
        this.messages.add(new Message(
                List.of(new Content("text", prompt)),
                RoleType.SYSTEM.getName()
        ));

        // 2. 히스토리 추가 (항상 존재)
        for (Chat chat : history) {
            this.messages.add(new Message(
                    List.of(new Content("text", chat.getMessage())),
                    chat.getRole().getName()
            ));
        }

        // 3. 현재 사용자 메시지
        this.messages.add(new Message(
                List.of(new Content("text", requestMessage.getMessage())),
                RoleType.USER.getName()
        ));
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
    private final List<Content> content;
    private final String role;
}

@Getter
@ToString
@AllArgsConstructor
class Content {
    private final String type;
    private final String text;
}