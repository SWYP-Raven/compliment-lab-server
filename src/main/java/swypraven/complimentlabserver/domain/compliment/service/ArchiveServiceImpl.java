package swypraven.complimentlabserver.domain.compliment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.compliment.entity.ChatCompliment;
import swypraven.complimentlabserver.domain.compliment.entity.SavedTodayCompliment;
import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.ChatCardArchiveItem;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.TodayArchiveItem;
import swypraven.complimentlabserver.domain.compliment.repository.ChatComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.repository.SavedTodayComplimentRepository;
import swypraven.complimentlabserver.domain.compliment.repository.TodayComplimentRepository;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.domain.friend.repository.ChatRepository;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;

import java.util.Map;

/**
 * 아카이브(일력/카드) 저장·조회·삭제 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveServiceImpl implements ArchiveService {

    private final SavedTodayComplimentRepository savedTodayRepo;
    private final ChatComplimentRepository chatComplimentRepo;
    private final TodayComplimentRepository todayRepo;
    private final ChatRepository chatRepo;
    private final UserRepository userRepo;

    // ===== 오늘의 칭찬(텍스트) =====
    @Override
    @Transactional
    public TodayArchiveItem saveToday(Long userId, Long todayId) {
        if (savedTodayRepo.existsByUserIdAndTodayComplimentId(userId, todayId)) {
            throw new IllegalStateException("이미 저장된 오늘의 칭찬입니다.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        TodayCompliment today = todayRepo.findById(todayId)
                .orElseThrow(() -> new IllegalArgumentException("TodayCompliment not found"));

        SavedTodayCompliment saved = SavedTodayCompliment.builder()
                .user(user)
                .todayCompliment(today)
                .build();
        saved = savedTodayRepo.save(saved);
        return mapToday(saved);
    }

    @Override
    public Page<TodayArchiveItem> listToday(Long userId, Pageable pageable) {
        return savedTodayRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToday);
    }

    @Override
    @Transactional
    public void removeToday(Long userId, Long savedId) {
        long deleted = savedTodayRepo.deleteByUserIdAndId(userId, savedId);
        if (deleted == 0) {
            throw new IllegalArgumentException("삭제할 항목이 없거나 권한이 없습니다.");
        }
    }

    // ===== 대화 카드(이미지) =====
    @Override
    @Transactional
    public ChatCardArchiveItem saveChatCard(Long userId, Long chatId, String imageUrl, String thumbUrl, Map<String, Object> payload) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        ChatCompliment entity = ChatCompliment.of(user, chat, imageUrl, thumbUrl, payload);
        entity = chatComplimentRepo.save(entity);

        return mapChatCard(entity);
    }

    @Override
    public Page<ChatCardArchiveItem> listChatCards(Long userId, String q, Pageable pageable) {
        if (q != null && !q.isBlank()) {
            return chatComplimentRepo.searchByUserAndChatMessage(userId, q, pageable)
                    .map(this::mapChatCard);
        }
        return chatComplimentRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapChatCard);
    }

    @Override
    @Transactional
    public void removeChatCard(Long userId, Long cardId) {
        long deleted = chatComplimentRepo.deleteByUserIdAndId(userId, cardId);
        if (deleted == 0) {
            throw new IllegalArgumentException("삭제할 카드가 없거나 권한이 없습니다.");
        }
    }

    // ===== mapper =====
    private TodayArchiveItem mapToday(SavedTodayCompliment e) {
        return TodayArchiveItem.builder()
                .id(e.getId())
                .todayId(e.getTodayCompliment().getId())
                .typeId(e.getTodayCompliment().getType().getId())
                .message(e.getTodayCompliment().getMessage())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private ChatCardArchiveItem mapChatCard(ChatCompliment e) {
        return ChatCardArchiveItem.builder()
                .id(e.getId())
                .chatId(e.getChat().getId())
                .imageUrl(e.getImageUrl())
                .thumbUrl(e.getThumbUrl())
                .payload(e.getPayload())
                .chatMessage(e.getChat().getMessage())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
