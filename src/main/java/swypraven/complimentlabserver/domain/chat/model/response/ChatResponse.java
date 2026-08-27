package swypraven.complimentlabserver.domain.chat.model.response;

import lombok.Getter;
import swypraven.complimentlabserver.domain.chat.entity.ChatRole;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.chat.entity.Chat;

import java.time.LocalDateTime;

@Getter
public class ChatResponse {

    public ChatResponse(Chat chat) {
        this.id = chat.getId();
        this.time = chat.getCreatedAt();
        this.message = chat.getMessage();
        this.name = chat.getFriend().getName();
        this.role = chat.getRole();
    }

    public ChatResponse(ChatCompliment chatCompliment) {
        this.id = chatCompliment.getChat().getId();
        this.time = LocalDateTime.from(chatCompliment.getCreatedAt());
        this.message = chatCompliment.getChat().getMessage();
        this.name = chatCompliment.getChat().getFriend().getName();
        this.role = chatCompliment.getChat().getRole();
    }

    private final Long id;
    private final LocalDateTime time;
    private final String message;
    private final String name;
    private final ChatRole role;
}
