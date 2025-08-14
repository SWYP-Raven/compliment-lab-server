package swypraven.complimentlabserver.domain.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseFriend;
import swypraven.complimentlabserver.domain.friend.service.FriendService;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResponseFriend>> create(@RequestBody RequestCreateFriend friend) {
        ResponseFriend character = friendService.create(friend);
        return ResponseEntity.status(201).body(ApiResponse.success(character, "201", "성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ResponseFriend>>> getFriends() {
        List<ResponseFriend> friends = friendService.getFriends();
        return ResponseEntity.status(200).body(ApiResponse.success(friends, "200", "성공"));
    }
}
