package swypraven.complimentlabserver.domain.compliment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.*;

import java.time.LocalDate;
import java.util.Map;

public interface ArchiveService {

    // ===== 오늘의 칭찬 (텍스트) =====
    TodayArchiveItem saveToday(Long userId, Long todayId);
    Page<TodayArchiveItem> listToday(Long userId, Pageable pageable);
    void removeToday(Long userId, Long savedId);

    // ===== 대화 카드 (텍스트 중심) =====
    ChatCardArchiveItem saveChatCard(Long userId, Long chatId, String title, String content, Map<String, Object> meta);
    Page<ChatCardArchiveItem> listChatCards(Long userId, String q, Pageable pageable);
    void removeChatCard(Long userId, Long cardId);

    // ===== 유저별 과거~오늘 조회 (미래 제외, 오늘 포함) =====
    Page<TodayArchiveItem> listTodayByUser(Long targetUserId, LocalDate from, LocalDate toOrToday, Pageable pageable);
}
