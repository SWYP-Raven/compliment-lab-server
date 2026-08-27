package swypraven.complimentlabserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.chat.api.ChatApi;
import swypraven.complimentlabserver.domain.chat.entity.Chat;
import swypraven.complimentlabserver.domain.chat.entity.ChatRole;
import swypraven.complimentlabserver.domain.chat.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.chat.model.response.ChatResponse;
import swypraven.complimentlabserver.domain.chat.model.response.ChatResponseSlice;
import swypraven.complimentlabserver.domain.chat.model.response.ResponseNavarClovaChat;
import swypraven.complimentlabserver.domain.chat.repository.ChatRepository;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.compliment.repository.ChatComplimentRepository;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.model.dto.LastMessageDto;
import swypraven.complimentlabserver.domain.friend.repository.FriendRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.exception.chat.ChatErrorCode;
import swypraven.complimentlabserver.global.exception.chat.ChatException;
import swypraven.complimentlabserver.global.exception.friend.FriendErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatApi chatApi;
    private final ChatRepository chatRepository;
    private final ChatComplimentRepository chatComplimentRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public LastMessageDto createUser(String message, Friend friend) {
        Chat chat = chatRepository.save(new Chat(message, ChatRole.ASSISTANT, friend));
        return new LastMessageDto(chat.getMessage(), chat.getCreatedAt());
    }

    @Transactional
    public ChatResponse send(Long friendId, RequestMessage requestMessage) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Chat> chatHistory = chatRepository.findLastChats(friend, pageable);
        Collections.reverse(chatHistory);

        ResponseNavarClovaChat chatResponse = chatApi.reply(friend, chatHistory, requestMessage);

        Chat chat = new Chat(requestMessage.getMessage(), ChatRole.USER, friend);
        Chat responseChat = new Chat(chatResponse.getMessage(), ChatRole.ASSISTANT, friend);

        chatRepository.save(chat);
        Chat savedChat = chatRepository.save(responseChat);
        return new ChatResponse(savedChat);
    }

    @Transactional(readOnly = true)
    public ChatResponseSlice findAllByFriend(Long friendId, LocalDateTime lastCreatedAt, int size) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.NOT_FOUND_FRIEND));

        Pageable pageable = PageRequest.of(0, size);
        Slice<Chat> chats = chatRepository.findNextChats(friend, lastCreatedAt, pageable);

        List<ChatResponse> chatResponses = new ArrayList<>(chats.getContent().stream()
                .map(ChatResponse::new)
                .toList());

        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }

    @Transactional
    public void saveMessage(Long userId, Long messageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND));

        if (chat.getRole() == ChatRole.USER) {
            throw new ChatException(ChatErrorCode.INVALID_SAVE_ROLE_TYPE);
        }

        ChatCompliment entity = ChatCompliment.of(
                user,
                chat,
                chat.getMessage(),
                chat.getRole().name(),
                null,
                null
        );

        chatComplimentRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public ChatResponseSlice findAllSavedChat(Long userId, int size, LocalDateTime lastCreatedAt) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Instant cursor = (lastCreatedAt != null)
                ? lastCreatedAt.atZone(KST).toInstant()
                : null;

        Slice<ChatCompliment> chats =
                chatComplimentRepository.findNextChats(user.getId(), cursor, pageable);

        List<ChatResponse> chatResponses = chats.getContent().stream()
                .map(ChatResponse::new)
                .toList();

        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }

    @Transactional(readOnly = true)
    public ChatResponse findLastChats(Friend friend) {
        Chat chat = chatRepository.findFirstByFriendOrderByCreatedAtDesc(friend)
                .orElseGet(() -> new Chat("", ChatRole.USER, friend));
        return new ChatResponse(chat);
    }
}
