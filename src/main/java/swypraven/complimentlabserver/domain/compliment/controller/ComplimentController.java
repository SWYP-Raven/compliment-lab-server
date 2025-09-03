package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.model.response.ComplimentListResponse;
import swypraven.complimentlabserver.domain.compliment.model.request.ComplimentLogPatchRequest;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compliments")
public class ComplimentController {

    private final ComplimentService complimentService;

    /** KST 기준, 사용자별 오늘의 칭찬 1건 (seed 기반) */
    @GetMapping("/today")
    public ResponseEntity<TodayDto> getToday(@AuthenticationPrincipal CustomUserPrincipal me) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(complimentService.getTodayForUser(me.id()));
    }

    /** 월별 조회: /compliments/month/2025-09 */
    @GetMapping("/month/{yearMonth}")
    public ResponseEntity<ComplimentListResponse> getMonth(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(complimentService.getMonth(me.id(), yearMonth));
    }

    /** 주/임의 범위 조회: /compliments/week?start=2025-09-01&end=2025-09-07 */
    @GetMapping("/week")
    public ResponseEntity<ComplimentListResponse> getWeek(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(complimentService.getRange(me.id(), start, end));
    }

    /**
     * 상태 변경(읽음/아카이브) – 해당 날짜의 로그를 upsert
     * PATCH /compliments/logs/2025-09-03
     * Body: { "is_read": true, "is_archived": false }
     */
    @PatchMapping("/logs/{date}")
    public ResponseEntity<Void> patchLog(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody ComplimentLogPatchRequest body
    ) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        boolean isRead = body.isRead();         // record 또는 DTO 게터에 맞춤
        boolean isArchived = body.isArchived();
        complimentService.upsertLog(me.id(), date, isRead, isArchived);
        return ResponseEntity.noContent().build();
    }

    /** 아카이브된 칭찬 월별 조회 */
    @GetMapping("/archived/{yearMonth}")
    public ResponseEntity<ComplimentListResponse> getArchivedByMonth(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(complimentService.getArchivedByMonth(me.id(), yearMonth, page, size));
    }
}
