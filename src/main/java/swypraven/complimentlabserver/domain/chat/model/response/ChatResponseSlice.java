package swypraven.complimentlabserver.domain.chat.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatResponseSlice {
    private List<ChatResponse> chats;
    private boolean hasNext;
}
