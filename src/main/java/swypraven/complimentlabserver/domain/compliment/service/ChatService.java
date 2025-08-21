package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.model.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.friend.service.FriendService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatApi chatApi;
    private final FriendService friendService;
    private final ChatRepository chatRepository;

    @Transactional
    public ResponseMessage send(Long friendId, RequestMessage requestMessage) {
        // 이전 대화 내역 불러오기
        Friend friend = friendService.getFriend(friendId);
        List<Chat> chatHistory = chatRepository.findChatsByFriend(friend);


        // AI에게 요청

        // 저장
//        Chat chat = new Chat(requestMessage, friend);
//        chatRepository.save(chat);
//        return new ResponseMessage(response);

        return null;
    }

    @Transactional(readOnly = true)
    public List<Chat> findAllByFriend(Long friendId) {
        Friend friend = friendService.getFriend(friendId);
//        List<Chat> chats = chatRepository.findAllByFriend(friend);
        return null;
    }

    private String reply(Friend friend, List<Chat> history, RequestMessage requestMessage) {
        return chatApi.reply(friend, requestMessage);
    }
}
