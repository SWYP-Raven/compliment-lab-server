package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.response.ResponseNavarClovaChat;
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
        ResponseNavarClovaChat chatResponse = chatApi.reply(friend, chatHistory, requestMessage);
        // 저장
        Chat chat = new Chat(requestMessage.getMessage(), RoleType.USER, friend);
        Chat responseChat = new Chat(chatResponse.getMessage(), RoleType.ASSISTANT, friend);

        chatRepository.save(chat);
        chatRepository.save(responseChat);

        return new ResponseMessage(chatResponse.getMessage());
    }

    @Transactional(readOnly = true)
    public List<Chat> findAllByFriend(Long friendId) {
        Friend friend = friendService.getFriend(friendId);
//        List<Chat> chats = chatRepository.findAllByFriend(friend);
        return null;
    }
}
