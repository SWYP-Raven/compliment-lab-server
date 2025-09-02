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

@RestController
@RequiredArgsConstructor
@RequestMapping("/archive")
public class ArchiveController {
    /**
     * 아카이브 관련 API를 제공하는 컨트롤러
     * 오늘의 칭찬과 대화 문장 아카이브 기능을 담당
     */
    private final ArchiveService archiveService;

    /**
     * 오늘의 칭찬을 아카이브에 저장
     *
     * @param me 인증된 사용자 정보
     * @param req 저장할 오늘의 칭찬 ID를 포함한 요청 객체
     * @return 저장된 오늘의 칭찬 아카이브 아이템
     */
    @PostMapping("/today")
    public ResponseEntity<TodayArchiveItem> saveToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveTodayRequest req) {
        TodayArchiveItem saved = archiveService.saveToday(me.id(), req.getTodayId());
        return ResponseEntity.ok(saved);
    }
    /**
     * 사용자의 오늘의 칭찬 아카이브 목록 조회 (페이징 처리)
     *
     * @param me 인증된 사용자 정보
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 페이징된 오늘의 칭찬 아카이브 목록
     */
    @GetMapping("/today")
    public ResponseEntity<PageResponse<TodayArchiveItem>> listToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TodayArchiveItem> result = archiveService.listToday(me.id(), pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }
    /**
     * 특정 오늘의 칭찬 아카이브 삭제
     *
     * @param me 인증된 사용자 정보
     * @param id 삭제할 아카이브 아이템 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/today/{id}")
    public ResponseEntity<Void> deleteToday(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id) {
        archiveService.removeToday(me.id(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 오늘의 칭찬 히스토리 조회 (과거~오늘까지, 미래 제외)
     * 특정 사용자의 히스토리 또는 본인 히스토리를 날짜 범위로 필터링하여 조회
     *
     * @param me 인증된 사용자 정보
     * @param userId 조회할 사용자 ID (null이면 본인)
     * @param from 시작 날짜 (null 허용)
     * @param to 종료 날짜 (null이면 오늘까지)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 조건에 맞는 오늘의 칭찬 아카이브 목록
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

        LocalDate toOrToday = (to != null ? to : LocalDate.now()); // 오늘 포함, 미래 제외

        Page<TodayArchiveItem> result = archiveService.listTodayByUser(
                targetUserId, from, toOrToday, pageable
        );
        return ResponseEntity.ok(PageResponse.from(result));
    }

    // ===== 대화 카드 관련 API =====

    /**
     * 대화 카드를 아카이브에 저장
     * 텍스트 중심
     * @param me 인증된 사용자 정보
     * @param req 저장할 대화 카드 정보 (채팅 ID, 제목, 내용, 메타데이터)
     * @return 저장된 대화 카드 아카이브 아이템
     */
    @PostMapping("/chat-cards")
    public ResponseEntity<ChatCardArchiveItem> saveChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @Valid @RequestBody SaveChatCardRequest req) {
        ChatCardArchiveItem saved = archiveService.saveChatCard(
                me.id(), req.getChatId(), req.getTitle(), req.getContent(), req.getMeta()
        );
        return ResponseEntity.ok(saved);
    }
    /**
     * 사용자의 대화 카드 아카이브 목록 조회 (검색 및 페이징 처리)
     *
     * @param me 인증된 사용자 정보
     * @param q 검색 쿼리 (제목이나 내용에서 검색, null 허용)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 페이징된 대화 카드 아카이브 목록
     */
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
    /**
     * 특정 대화 카드 아카이브 삭제
     *
     * @param me 인증된 사용자 정보
     * @param id 삭제할 대화 카드 아카이브 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/chat-cards/{id}")
    public ResponseEntity<Void> deleteChatCard(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @PathVariable Long id) {
        archiveService.removeChatCard(me.id(), id);
        return ResponseEntity.noContent().build();
    }
}
