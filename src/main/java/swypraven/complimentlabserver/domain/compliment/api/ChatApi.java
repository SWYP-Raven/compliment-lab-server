package swypraven.complimentlabserver.domain.compliment.api;

import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.response.ResponseNavarClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

import java.util.List;

@Service
public interface ChatApi {
     ResponseNavarClovaChat reply(Friend friend, List<Chat> history, RequestMessage message);
}
