package swypraven.complimentlabserver.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.chat.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.chat.model.response.ChatResponse;
import swypraven.complimentlabserver.domain.chat.model.response.ChatResponseSlice;
import swypraven.complimentlabserver.domain.chat.service.ChatService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @PathVariable Long friendId,
            @RequestBody RequestMessage requestMessage
    ) {
        ChatResponse response = chatService.send(friendId, requestMessage);
        return ResponseEntity.status(201).body(ApiResponse.success(response, "201", "성공"));
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ChatResponseSlice>> getMessages(
            @PathVariable Long friendId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatResponseSlice response = chatService.findAllByFriend(friendId, lastCreatedAt, size);
        return ResponseEntity.ok(ApiResponse.success(response, "200", "조회 성공"));
    }

    @PostMapping("/save/{messageId}")
    public ResponseEntity<?> saveMessage(
            @PathVariable("messageId") Long messageId,
            @AuthenticationPrincipal CustomUserPrincipal me
    ) {
        chatService.saveMessage(me.id(), messageId);
        return ResponseEntity.ok(ApiResponse.success("200", "저장 성공"));
    }

    @GetMapping("/save")
    public ResponseEntity<?> getSavedMessages(
            @AuthenticationPrincipal CustomUserPrincipal me,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatResponseSlice response = chatService.findAllSavedChat(me.id(), size, lastCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(response, "200", "조회 성공"));
    }
}
