package swypraven.complimentlabserver.domain.friend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.request.RequestUpdateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseFriend;
import swypraven.complimentlabserver.domain.friend.service.FriendService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResponseFriend>> create(
            @Valid @RequestBody RequestCreateFriend friend,
            @AuthenticationPrincipal CustomUserPrincipal currentUser
    ) {
        ResponseFriend character = friendService.create(currentUser.id(), friend);
        return ResponseEntity.status(201).body(ApiResponse.success(character, "201", "성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResponseFriend>>> getFriends(
            @AuthenticationPrincipal CustomUserPrincipal currentUser
    ) {
        List<ResponseFriend> friends = friendService.getFriends(currentUser.id());
        return ResponseEntity.status(200).body(ApiResponse.success(friends, "200", "성공"));
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ResponseFriend>> updateFriend(
            @PathVariable Long friendId,
            @Valid @RequestBody RequestUpdateFriend request,
            @AuthenticationPrincipal CustomUserPrincipal currentUser
    ) {
        ResponseFriend friend = friendService.updateFriend(friendId, currentUser.id(), request);
        return ResponseEntity.status(200).body(ApiResponse.success(friend, "200", "성공"));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ResponseFriend>> deleteFriend(
            @PathVariable Long friendId,
            @AuthenticationPrincipal CustomUserPrincipal currentUser
    ) {
        friendService.delete(friendId, currentUser.id());
        return ResponseEntity.status(200).body(ApiResponse.success("200", "성공"));
    }
}
