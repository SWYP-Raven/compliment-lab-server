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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveServiceImpl implements ArchiveService {

    private final SavedTodayComplimentRepository savedTodayRepo;
    private final ChatComplimentRepository chatComplimentRepo;
    private final TodayComplimentRepository todayRepo;
    private final ChatRepository chatRepo;
    private final UserRepository userRepo;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

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

    // ArchiveServiceImpl.java (일부만)
    @Override
    public Page<TodayArchiveItem> listToday(Long userId, Pageable pageable) {
        return savedTodayRepo
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
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

    // ===== 대화 카드(텍스트 중심) =====
    @Override
    @Transactional
    public ChatCardArchiveItem saveChatCard(Long userId, Long chatId, String title, String content, Map<String, Object> meta) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // (선택) 소유권 검사: 내 friend의 chat만 허용
        if (!chat.getFriend().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot archive someone else's chat.");
        }

        ChatCompliment entity = ChatCompliment.of(user, chat, title, content, meta);
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
        long deleted = chatComplimentRepo.deleteByUserIdAndId(userId, cardId);
        if (deleted == 0) {
            throw new IllegalArgumentException("삭제할 카드가 없거나 권한이 없습니다.");
        }
    }

    // ===== 유저별 과거~오늘 조회(미래 제외, 오늘 포함) =====
    @Override
    public Page<TodayArchiveItem> listTodayByUser(Long targetUserId, LocalDate from, LocalDate toOrToday, Pageable pageable) {
        LocalDate upper = (toOrToday != null) ? toOrToday : LocalDate.now(KST); // 오늘 포함
        Instant startInclusive = (from != null) ? from.atStartOfDay(KST).toInstant() : Instant.EPOCH;
        Instant endInclusive = upper.plusDays(1).atStartOfDay(KST).toInstant().minusMillis(1);

        // Repository 쿼리는 아래 시그니처 중 하나가 필요합니다.
        // 1) 메서드 이름 기반:
        // Page<SavedTodayCompliment> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, Instant start, Instant end, Pageable p);
        //
        // 2) 또는 @Query 기반 findHistory(userId, start, end, pageable)

        return savedTodayRepo
                .findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(targetUserId, startInclusive, endInclusive, pageable)
                .map(this::mapToday);
    }

    // ===== mappers =====
    private TodayArchiveItem mapToday(SavedTodayCompliment e) {
        return TodayArchiveItem.builder()
                .id(e.getId())
                .todayId(e.getTodayCompliment().getId())
                .typeId(e.getTodayCompliment().getType().getId())
                .message(e.getTodayCompliment().getMessage())
                .createdAt(LocalDateTime.ofInstant(Instant.from(e.getCreatedAt()), KST)) // ✅ 변환
                .build();
    }

    private ChatCardArchiveItem mapChatCard(ChatCompliment e) {
        return ChatCardArchiveItem.builder()
                .id(e.getId())
                .chatId(e.getChat().getId())
                .title(e.getTitle())
                .content(e.getContent())
                .meta(e.getMeta())
                .chatMessage(e.getChat().getMessage())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
