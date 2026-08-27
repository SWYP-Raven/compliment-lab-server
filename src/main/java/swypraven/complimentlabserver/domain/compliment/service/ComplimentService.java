package swypraven.complimentlabserver.domain.compliment.service;

import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

public interface ComplimentService {

    ZoneId KST = ZoneId.of("Asia/Seoul");

    default TodayDto getTodayForUser(Long userId) {
        return getTodayForUserOn(userId, LocalDate.now(KST));
    }

    TodayDto getTodayForUserOn(Long userId, LocalDate date);

    ComplimentListResponse getMonth(Long userId, YearMonth ym);

    ComplimentListResponse getRange(Long userId, LocalDate start, LocalDate end);

    void upsertLog(Long userId, LocalDate date, boolean isRead, boolean isArchived);

    ComplimentListResponse getArchivedByMonth(Long userId, YearMonth ym, int page, int size);
}
