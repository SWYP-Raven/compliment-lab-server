package swypraven.complimentlabserver.domain.compliment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.*;

import java.util.Map;

public interface ArchiveService {

    // 오늘의 칭찬(텍스트)
    TodayArchiveItem saveToday(Long userId, Long todayId);
    Page<TodayArchiveItem> listToday(Long userId, Pageable pageable);
    void removeToday(Long userId, Long savedId);

    // 대화카드(이미지)
    ChatCardArchiveItem saveChatCard(Long userId, Long chatId, String imageUrl, String thumbUrl, Map<String,Object> payload);
    Page<ChatCardArchiveItem> listChatCards(Long userId, String q, Pageable pageable);
    void removeChatCard(Long userId, Long cardId);
}
