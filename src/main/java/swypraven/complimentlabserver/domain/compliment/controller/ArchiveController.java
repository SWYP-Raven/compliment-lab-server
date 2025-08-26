package swypraven.complimentlabserver.domain.compliment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.request.ArchiveRequests.*;
import swypraven.complimentlabserver.domain.compliment.model.response.ArchiveDtos.*;
import swypraven.complimentlabserver.domain.compliment.service.ArchiveService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;
import swypraven.complimentlabserver.global.response.PageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    // ===== 오늘의 칭찬 =====
    @PostMapping("/today")
    public ResponseEntity<TodayArchiveItem> saveToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveTodayRequest req) {
        TodayArchiveItem saved = archiveService.saveToday(me.id(), req.getTodayId()); // DTO가 클래스면 getTodayId()
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/today")
    public ResponseEntity<PageResponse<TodayArchiveItem>> listToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TodayArchiveItem> result = archiveService.listToday(me.id(), pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @DeleteMapping("/today/{id}")
    public ResponseEntity<Void> deleteToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id) {
        archiveService.removeToday(me.id(), id);
        return ResponseEntity.noContent().build();
    }

    // ===== 대화 카드 =====
    @PostMapping("/chat-cards")
    public ResponseEntity<ChatCardArchiveItem> saveChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveChatCardRequest req) {
        ChatCardArchiveItem saved = archiveService.saveChatCard(
                me.id(), req.getChatId(), req.getImageUrl(), req.getThumbUrl(), req.getPayload());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/chat-cards")
    public ResponseEntity<PageResponse<ChatCardArchiveItem>> listChatCards(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatCardArchiveItem> result = archiveService.listChatCards(me.id(), q, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @DeleteMapping("/chat-cards/{id}")
    public ResponseEntity<Void> deleteChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id) {
        archiveService.removeChatCard(me.id(), id);
        return ResponseEntity.noContent().build();
    }
}
