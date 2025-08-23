package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.request.RequestMessage;
import swypraven.complimentlabserver.domain.compliment.model.response.ResponseMessage;
import swypraven.complimentlabserver.domain.compliment.service.ChatService;
import swypraven.complimentlabserver.domain.friend.entity.Chat;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.util.List;


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
    public ResponseEntity<?> sendMessage(@PathVariable Long friendId) {
        List<Chat> chats = chatService.findAllByFriend(friendId);
        return ResponseEntity.status(200).body(chats);
    }

}
