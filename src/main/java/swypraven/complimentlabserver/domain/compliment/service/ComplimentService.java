// ComplimentService.java
package swypraven.complimentlabserver.domain.compliment.service;

import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;

import java.time.*;

public interface ComplimentService {

    /** KST 타임존 상수 (seed 계산 시 사용) */
    ZoneId KST = ZoneId.of("Asia/Seoul");
    /**
     * 오늘(KST) 날짜 기준으로 사용자별 오늘의 칭찬 1건을 반환합니다.
     * <p>기본 구현은 {@link #getTodayForUserOn(Long, LocalDate)}에 KST 오늘 날짜를 전달합니다.</p>
     */
    default TodayDto getTodayForUser(Long userId) {
        return getTodayForUserOn(userId, LocalDate.now(KST));
    }
    /**
     * 주어진 KST 날짜 기준으로 사용자별 오늘의 칭찬 1건을 반환합니다.
     * <p>seed = toEpochDay(KST 날짜) + userId (+ optional salt) 등으로 구현하세요.</p>
     */
    TodayDto getTodayForUserOn(Long userId, LocalDate now);
    /** 월별 조회 */
    ComplimentListResponse getMonth(Long userId, YearMonth ym);
    /** 범위 조회 (둘 다 KST 날짜로 가정) */
    ComplimentListResponse getRange(Long userId, LocalDate start, LocalDate end);
    /** 읽음/아카이브 상태 upsert */
    void upsertLog(Long userId, LocalDate date, boolean isRead, boolean isArchived);
    /** 아카이브된 칭찬 월별 조회 (페이지네이션) */
    ComplimentListResponse getArchivedByMonth(Long userId, YearMonth ym, int page, int size);


}

