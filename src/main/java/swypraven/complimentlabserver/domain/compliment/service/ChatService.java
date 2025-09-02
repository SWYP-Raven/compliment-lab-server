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
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.exception.chat.ChatErrorCode;
import swypraven.complimentlabserver.global.exception.chat.ChatException;
import swypraven.complimentlabserver.global.exception.friend.FriendErrorCode;
import swypraven.complimentlabserver.global.exception.friend.FriendException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.ZoneId;
import java.util.Collections;

import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.util.List;
import java.util.Map;

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

        Pageable pageable = PageRequest.of(0, size); // 정렬은 JPQL에서 처리
        Slice<Chat> chats = chatRepository.findNextChats(friend, lastCreatedAt, pageable);

        List<ChatResponse> chatResponses = new ArrayList<>(chats.getContent().stream()
                .map(ChatResponse::new)
                .toList());

        // 최신 메시지가 아래로 가도록 역순으로 변환
        Collections.reverse(chatResponses);

        return ChatResponseSlice.of(chatResponses, chats.hasNext());
    }



    @Transactional
    public void saveMessage(CustomUserDetails userDetails, Long messageId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.NOT_FOUND));

        if (chat.getRole() == RoleType.USER) {
            throw new ChatException(ChatErrorCode.INVALID_SAVE_ROLE_TYPE);
        }

        // 카드 제목/본문/메타로 저장 (본문은 원문 대화 메시지를 기본값으로)
        String title = null; // 필요시 클라이언트에서 받아도 됨
        String content = chat.getMessage(); // 원문 대화 내용을 본문 기본값으로
        Map<String, Object> meta = Map.of(); // 필요 없으면 빈 맵

        ChatCompliment entity = ChatCompliment.of(user, chat, title, content, meta);
        chatComplimentRepository.save(entity);
    }


    @Transactional(readOnly = true)
    public ChatResponseSlice findAllSavedChat(CustomUserDetails userDetails, int size, LocalDateTime lastCreatedAt) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // LocalDateTime -> Instant(KST 기준) 변환 (null 허용)
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
        Chat chat = chatRepository.findFirstByFriendOrderByCreatedAtDesc(friend).orElseGet(() -> new Chat("", RoleType.USER, friend));
        return new ChatResponse(chat);
    }
}
