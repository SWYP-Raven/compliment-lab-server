package swypraven.complimentlabserver.domain.compliment.model.dto;

import lombok.Getter;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.friend.entity.Chat;

import java.time.Instant;
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
        this.time = LocalDateTime.from(Instant.from(chatCompliment.getCreatedAt()));
        this.message = chatCompliment.getChat().getMessage();
        this.name = chatCompliment.getChat().getFriend().getName();
        this.role = chatCompliment.getChat().getRole();
    }

    private final Long id;
    private final LocalDateTime time;
    private final String message;
    private final String name;
    private final RoleType role;
}
