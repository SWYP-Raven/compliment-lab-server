// ComplimentService.java
package swypraven.complimentlabserver.domain.compliment.service;

import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;

import java.time.*;

public interface ComplimentService {
    TodayDto getTodayForUser(Long userId);
    ComplimentListResponse getMonth(Long userId, YearMonth ym);
    ComplimentListResponse getRange(Long userId, LocalDate start, LocalDate end);
    void upsertLog(Long userId, LocalDate date, boolean isRead, boolean isArchived);

    ComplimentListResponse getArchivedByMonth(Long userId, YearMonth ym, int page, int size);


}

