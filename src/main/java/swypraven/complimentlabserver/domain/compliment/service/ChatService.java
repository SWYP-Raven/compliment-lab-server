package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.friend.service.FriendService;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatApi chatApi;
    private final FriendService friendService;
    private final ChatRepository chatRepository;

    @Transactional
    public ResponseMessage send(Long friendId, RequestMessage requestMessage) {


        Friend friend = friendService.getFriend(friendId);

        // 저장
        Chat chat = new Chat(requestMessage, friend);
        chatRepository.save(chat);
        String response = reply(friend, requestMessage);

        return new ResponseMessage(response);
    }

    private String reply(Friend friend, RequestMessage requestMessage) {
        return "테스트";
    }
}
