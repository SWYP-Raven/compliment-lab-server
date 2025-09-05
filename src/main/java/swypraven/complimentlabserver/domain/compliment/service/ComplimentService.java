// ComplimentService.java
package swypraven.complimentlabserver.domain.compliment.service;

import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;

import java.time.*;

public interface ComplimentService {

    ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 오늘(KST 기준) */
    default TodayDto getTodayForUser(Long userId) {
        return getTodayForUserOn(userId, LocalDate.now(KST));
    }

    /** 특정 날짜(KST) 기준 */
    TodayDto getTodayForUserOn(Long userId, LocalDate date);

    /** 월별 조회 */
    ComplimentListResponse getMonth(Long userId, YearMonth ym);

    /** 범위 조회 */
    ComplimentListResponse getRange(Long userId, LocalDate start, LocalDate end);

    /** 읽음/아카이브 상태 upsert */
    void upsertLog(Long userId, LocalDate date, boolean isRead, boolean isArchived);

    /** 아카이브 월별 조회 */
    ComplimentListResponse getArchivedByMonth(Long userId, YearMonth ym, int page, int size);
}
