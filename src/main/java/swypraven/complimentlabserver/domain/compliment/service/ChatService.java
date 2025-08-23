package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.api.ChatApi;
import swypraven.complimentlabserver.domain.compliment.api.naver.RoleType;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponse;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponseSlice;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.response.ResponseNavarClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.friend.service.FriendService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatApi chatApi;
    private final FriendService friendService;
    private final ChatRepository chatRepository;

    @Transactional
    public ResponseMessage send(Long friendId, RequestMessage requestMessage) {
        // 친구 정보
        Friend friend = friendService.getFriend(friendId);

        // 최근 20개 가져오기
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Chat> chatHistory = chatRepository.findLastChats(friend, pageable);

        // 과거 → 최신 순서로 정렬 (AI가 맥락 이해 가능하게)
        Collections.reverse(chatHistory);

        // AI 응답 생성
        ResponseNavarClovaChat chatResponse = chatApi.reply(friend, chatHistory, requestMessage);

        // 메시지 저장
        Chat chat = new Chat(requestMessage.getMessage(), RoleType.USER, friend);
        Chat responseChat = new Chat(chatResponse.getMessage(), RoleType.ASSISTANT, friend);

        chatRepository.save(chat);
        chatRepository.save(responseChat);

        return new ResponseMessage(chatResponse.getMessage());
    }

    @Transactional(readOnly = true)
    public ChatResponseSlice findAllByFriend(Long friendId, LocalDateTime lastCreatedAt, int size) {
        Friend friend = friendService.getFriend(friendId);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Chat> chats = chatRepository.findNextChats(friend, lastCreatedAt, pageable);

        List<ChatResponse> chatResponses = chats.getContent().stream()
                .map(ChatResponse::new)
                .toList();

        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }

}
