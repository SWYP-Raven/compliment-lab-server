package swypraven.complimentlabserver.domain.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.request.RequestUpdateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseFriend;
import swypraven.complimentlabserver.domain.friend.service.FriendService;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResponseFriend>> create(
            @RequestBody RequestCreateFriend friend,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ResponseFriend character = friendService.create(userDetails, friend);
        return ResponseEntity.status(201).body(ApiResponse.success(character, "201", "성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResponseFriend>>> getFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ResponseFriend> friends = friendService.getFriends(userDetails);
        return ResponseEntity.status(200).body(ApiResponse.success(friends, "200", "성공"));
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ResponseFriend>> updateFriend(
            @PathVariable Long friendId,
            @RequestBody RequestUpdateFriend request
    ) {
        ResponseFriend friend = friendService.updateFriend(friendId, request);
        return ResponseEntity.status(200).body(ApiResponse.success(friend, "200", "성공"));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse<ResponseFriend>> deleteFriend(@PathVariable Long friendId) {
        friendService.delete(friendId);
        return ResponseEntity.status(200).body(ApiResponse.success("200", "성공"));
    }

}
