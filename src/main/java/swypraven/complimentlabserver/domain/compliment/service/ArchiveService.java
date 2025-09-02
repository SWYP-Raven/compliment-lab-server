package swypraven.complimentlabserver.domain.compliment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swypraven.complimentlabserver.domain.compliment.model.request.ArchiveRequests;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.*;

import java.time.LocalDate;

public interface ArchiveService {

    // ===== 오늘의 칭찬 (seed 기반) =====
    // ArchiveService
    TodayArchiveItem saveTodayBySeed(Long userId, ArchiveRequests.SaveTodayBySeedRequest req);
    ChatCardArchiveItem saveChatCardBySeed(Long userId, ArchiveRequests.SaveChatCardBySeedRequest req);
    Page<TodayArchiveItem> listToday(Long userId, Pageable pageable);
    Page<TodayArchiveItem> listTodayByUser(Long targetUserId, LocalDate from, LocalDate toOrToday, Pageable pageable);
    Page<ChatCardArchiveItem> listChatCards(Long userId, String q, Pageable pageable);
    void removeToday(Long userId, Long id);
    void removeChatCard(Long userId, Long id);
}
