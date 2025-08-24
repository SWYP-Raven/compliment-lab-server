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
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponse;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponseSlice;
import swypraven.complimentlabserver.domain.compliment.model.dto.naver.response.ResponseNavarClovaChat;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.compliment.repository.ChatComplimentRepository;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.friend.repository.FriendRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.exception.chat.ChatErrorCode;
import swypraven.complimentlabserver.global.exception.chat.ChatException;
import swypraven.complimentlabserver.global.exception.friend.FriendErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final ChatRepository chatRepository;
    private final ChatComplimentRepository chatComplimentRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;


    @Transactional
    public ResponseMessage send(Long friendId, RequestMessage requestMessage) {
        // 친구 정보
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));

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
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Chat> chats = chatRepository.findNextChats(friend, lastCreatedAt, pageable);

        List<ChatResponse> chatResponses = chats.getContent().stream()
                .map(ChatResponse::new)
                .toList();

        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }


    @Transactional
    public void saveMessage(Long messageId) {
        // TODO: 유저 정보 가져오기
        User user = userRepository.findById(1L).get();

        Chat chat = chatRepository.findById(messageId).orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND));

        if(chat.getRole() == RoleType.USER) {
            throw new ChatException(ChatErrorCode.INVALID_SAVE_ROLE_TYPE);
        }

        chatComplimentRepository.save(new ChatCompliment(user, chat));
    }

    @Transactional(readOnly = true)
    public ChatResponseSlice findAllSavedChat(int size, LocalDateTime lastCreatedAt) {
        // TODO: 유저 정보 가져오기
        User user = userRepository.findById(1L).get();

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<ChatCompliment> chats = chatComplimentRepository.findNextChats(user, lastCreatedAt, pageable);

        List<ChatResponse> chatResponses = chats.getContent().stream()
                .map(ChatResponse::new)
                .toList();
        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }

    @Transactional(readOnly = true)
    public ChatResponse findLastChats(Friend friend) {
        Chat chat = chatRepository.findFirstByFriendOrderByCreatedAtDesc(friend).orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND));
        return new ChatResponse(chat);
    }
}
