package swypraven.complimentlabserver.domain.compliment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.request.ArchiveRequests.*;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.*;
import swypraven.complimentlabserver.domain.compliment.service.ArchiveService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;
import swypraven.complimentlabserver.global.response.PageResponse;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    // ===================== 오늘의 칭찬 (seed 기반) =====================

    /**
     * 오늘의 칭찬을 아카이브에 저장 (seed 기반으로 이미 생성된 문장을 보존)
     *
     * @param me  인증 사용자
     * @param req seed 및 생성 결과(텍스트)와 메타데이터
     */
    @PostMapping("/today")
    public ResponseEntity<TodayArchiveItem> saveToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveTodayBySeedRequest req
    ) {
        // 생성은 외부 서비스에서 끝났다는 가정: 여기서는 보존만
        TodayArchiveItem saved = archiveService.saveTodayBySeed(me.id(), req);
        return ResponseEntity.ok(saved);
    }

    /**
     * 오늘의 칭찬 목록 조회 (내 것)
     */
    @GetMapping("/today")
    public ResponseEntity<PageResponse<TodayArchiveItem>> listToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TodayArchiveItem> result = archiveService.listToday(me.id(), pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    /**
     * 과거~오늘까지 히스토리 조회(미래 제외), 본인 또는 특정 userId
     */
    @GetMapping("/today/history")
    public ResponseEntity<PageResponse<TodayArchiveItem>> listTodayHistory(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long targetUserId = (userId != null ? userId : me.id());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        LocalDate toOrToday = (to != null ? to : LocalDate.now());
        Page<TodayArchiveItem> result = archiveService.listTodayByUser(targetUserId, from, toOrToday, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    /**
     * 특정 오늘의 칭찬 삭제
     */
    @DeleteMapping("/today/{id}")
    public ResponseEntity<Void> deleteToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id
    ) {
        archiveService.removeToday(me.id(), id);
        return ResponseEntity.noContent().build();
    }


    // ===================== 대화 카드(문장 보존, seed 메타 포함 가능) =====================

    /**
     * 대화 중 마음에 드는 문장을 아카이브로 저장
     * (title/content/meta → message/role/seed/model/temperature/styleId 중심으로 변경)
     */
    @PostMapping("/chat-cards")
    public ResponseEntity<ChatCardArchiveItem> saveChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveChatCardBySeedRequest req
    ) {
        ChatCardArchiveItem saved = archiveService.saveChatCardBySeed(me.id(), req);
        return ResponseEntity.ok(saved);
    }

    /**
     * 대화 카드 목록(검색/페이징)
     */
    @GetMapping("/chat-cards")
    public ResponseEntity<PageResponse<ChatCardArchiveItem>> listChatCards(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatCardArchiveItem> result = archiveService.listChatCards(me.id(), q, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }


    @GetMapping("/chat-cards/{yearMonth}")
    public ResponseEntity<ChatCardArchiveItemList> getChatCardsByMonth(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth yearMonth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatCardArchiveItemList result = archiveService.getArchivedByMonth(me.id(), yearMonth, page, size);
        return ResponseEntity.ok(result);
    }




    /**
     * 대화 카드 삭제
     */
    @DeleteMapping("/chat-cards/{id}")
    public ResponseEntity<Void> deleteChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id
    ) {
        archiveService.removeChatCard(me.id(), id);
        return ResponseEntity.noContent().build();
    }
}
