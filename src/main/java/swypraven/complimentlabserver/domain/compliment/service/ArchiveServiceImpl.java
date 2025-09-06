package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;
import swypraven.complimentlabserver.domain.compliment.model.request.ArchiveRequests;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.ChatCardArchiveItem;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.TodayArchiveItem;
import swypraven.complimentlabserver.domain.compliment.repository.ChatComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.repository.SavedTodayComplimentRepository;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.exception.archive.ArchiveErrorCode;
import swypraven.complimentlabserver.global.exception.archive.ArchiveException;
import swypraven.complimentlabserver.global.exception.chat.ChatException;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.time.*;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveServiceImpl implements ArchiveService {

    private final SavedTodayComplimentRepository savedTodayRepo;
    private final ChatComplimentRepository chatComplimentRepo;
    private final ChatRepository chatRepo;
    private final UserRepository userRepo;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    // ========================= 오늘의 칭찬 (seed 기반) =========================
    @Transactional
    public TodayArchiveItem saveToday(
            Long userId,
            String text,
            Long seed
    ) {
        if (text == null || text.isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.MESSAGE_EMPTY);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        SavedTodayCompliment saved = SavedTodayCompliment.builder()
                .user(user)
                .text(text)
                .seed(seed)
                // createdAt은 @PrePersist(Instant.now())에서 자동 세팅 권장
                .build();

        saved = savedTodayRepo.save(saved);
        return mapToday(saved);
    }

    @Override
    @Transactional
    public TodayArchiveItem saveTodayBySeed(Long userId, ArchiveRequests.SaveTodayBySeedRequest req) {
        String text = req.getText();
        if (text == null || text.isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.TEXT_EMPTY);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.USER_NOT_FOUND));

        SavedTodayCompliment saved = SavedTodayCompliment.builder()
                .user(user)
                .text(text)
                .seed(req.getSeed())
                .build(); // createdAt은 @PrePersist(Instant.now())로 세팅

        saved = savedTodayRepo.save(saved);
        return mapToday(saved);
    }

    @Override
    @Transactional
    public ChatCardArchiveItem saveChatCardBySeed(Long userId, ArchiveRequests.SaveChatCardBySeedRequest req) {
        if (req.getMessage() == null || req.getMessage().isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.MESSAGE_EMPTY);
        }
        if (req.getRole() == null || req.getRole().isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.ROLE_EMPTY);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.USER_NOT_FOUND));
        Chat chat = chatRepo.findById(req.getChatId())
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.CHAT_NOT_FOUND));

        // 소유권 체크
        if (chat.getFriend() == null || chat.getFriend().getUser() == null
                || !Objects.equals(chat.getFriend().getUser().getId(), userId)) {
            throw new ArchiveException(ArchiveErrorCode.CHAT_NOT_FOUND_OR_FORBIDDEN);
        }



        ChatCompliment entity = ChatCompliment.builder()
                .user(user)
                .chat(chat)
                .message(req.getMessage())
                .role(req.getRole())              // "USER" | "ASSISTANT"
                .seed(req.getSeed())
                .metaJson(req.getMetaJson())
                .build(); // createdAt은 @PrePersist로 세팅

        entity = chatComplimentRepo.save(entity);
        return mapChatCard(entity);
    }

    @Override
    public Page<TodayArchiveItem> listToday(Long userId, Pageable pageable) {
        return savedTodayRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToday);
    }

    @Override
    @Transactional
    public void removeToday(Long userId, Long savedId) {
        int deleted = savedTodayRepo.deleteByUserIdAndId(userId, savedId);
        if (deleted == 0) throw new ArchiveException(ArchiveErrorCode.TODAY_NOT_FOUND_OR_FORBIDDEN);
    }

    @Transactional
    public ChatCardArchiveItem saveChatCard(
            Long userId,
            Long chatId,
            String message,
            String role,           // "USER" | "ASSISTANT"
            Long seed,
            String metaJson
    ) {
        if (message == null || message.isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.MESSAGE_EMPTY);
        }
        if (role == null || role.isBlank()) {
            throw new ArchiveException(ArchiveErrorCode.ROLE_EMPTY);
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.USER_NOT_FOUND));
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new ArchiveException(ArchiveErrorCode.CHAT_NOT_FOUND));

        if (chat.getFriend() == null || chat.getFriend().getUser() == null
                || !Objects.equals(chat.getFriend().getUser().getId(), userId)) {
            throw new ArchiveException(ArchiveErrorCode.CHAT_NOT_FOUND_OR_FORBIDDEN);
        }


        ChatCompliment entity = ChatCompliment.builder()
                .user(user)
                .chat(chat)
                .message(message)
                .role(role)
                .seed(seed)
                .metaJson(metaJson)
                .build();

        entity = chatComplimentRepo.save(entity);
        return mapChatCard(entity);
    }

    @Override
    public Page<ChatCardArchiveItem> listChatCards(Long userId, String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return chatComplimentRepo.searchByUserAndKeyword(userId, q, pageable)
                    .map(this::mapChatCard);
        }
        return chatComplimentRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapChatCard);
    }

    @Override
    @Transactional
    public void removeChatCard(Long userId, Long cardId) {
        int deleted = chatComplimentRepo.deleteByUserIdAndId(userId, cardId);
        if (deleted == 0) throw new ArchiveException(ArchiveErrorCode.CARD_NOT_FOUND_OR_FORBIDDEN);
    }

    // ===== 유저별 과거~오늘 조회(미래 제외, 오늘 포함) =====
    @Override
    public Page<TodayArchiveItem> listTodayByUser(
            Long targetUserId, LocalDate from, LocalDate toOrToday, Pageable pageable
    ) {
        LocalDate upper = (toOrToday != null) ? toOrToday : LocalDate.now(KST);   // 오늘 포함
        Instant fromStart = (from != null) ? from.atStartOfDay(KST).toInstant() : null;
        Instant toEndExclusive = upper.plusDays(1).atStartOfDay(KST).toInstant(); // [start, end)

        // 레포에 있는 findHistory 사용 (end는 배타)
        return savedTodayRepo.findHistory(targetUserId, fromStart, toEndExclusive, pageable)
                .map(this::mapToday);

    }

    // ============================== mappers ==============================
    private TodayArchiveItem mapToday(SavedTodayCompliment e) {
        return TodayArchiveItem.builder()
                .id(e.getId())
                .text(e.getText())
                .seed(e.getSeed())
                .createdAt(LocalDateTime.ofInstant(e.getCreatedAt(), KST)) // Instant → LDT(KST)
                .build();
    }

    private ChatCardArchiveItem mapChatCard(ChatCompliment e) {
        return ChatCardArchiveItem.builder()
                .id(e.getId())
                .chatId(e.getChat().getId())
                .message(e.getMessage())
                .role(e.getRole())
                .seed(e.getSeed())
                .metaJson(e.getMetaJson())
                .createdAt(LocalDateTime.ofInstant(e.getCreatedAt(), KST)) // Instant/LDT → LDT(KST)
                .build();
    }
}

