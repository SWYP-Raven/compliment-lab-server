package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.dto.ChatResponseSlice;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.compliment.service.ChatService;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/friend/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ResponseMessage>> sendMessage(@PathVariable Long friendId, @RequestBody RequestMessage requestMessage) {
        ResponseMessage response = chatService.send(friendId, requestMessage);
        return ResponseEntity.status(201).body(ApiResponse.success(response, "201", "성공"));
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ChatResponseSlice>> getMessages(
            @PathVariable Long friendId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") int size)
    {
        ChatResponseSlice response = chatService.findAllByFriend(friendId, lastCreatedAt, size);
        return ResponseEntity.ok(ApiResponse.success(response, "200", "조회 성공"));
    }

    @PostMapping("/save/{messageId}")
    public ResponseEntity<?> saveMessage(
            @PathVariable("messageId") Long messageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        chatService.saveMessage(userDetails, messageId);
        return ResponseEntity.ok(ApiResponse.success("200", "저장 성공"));
    }

    @GetMapping("/save")
    public ResponseEntity<?> getSavedMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") int size
    ) {
        ChatResponseSlice response = chatService.findAllSavedChat(userDetails, size, lastCreatedAt);
        return ResponseEntity.ok(ApiResponse.success(response, "200", "조회 성공"));
    }
}
