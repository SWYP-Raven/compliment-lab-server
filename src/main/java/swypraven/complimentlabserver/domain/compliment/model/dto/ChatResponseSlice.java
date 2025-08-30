package swypraven.complimentlabserver.domain.compliment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatResponseSlice {
    private List<ChatResponse> chats;
    private boolean hasNext;
}
