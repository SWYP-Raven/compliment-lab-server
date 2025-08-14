package swypraven.complimentlabserver.domain.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swypraven.complimentlabserver.domain.friend.model.request.RequestCreateFriend;
import swypraven.complimentlabserver.domain.friend.model.response.ResponseCreateFriend;
import swypraven.complimentlabserver.domain.friend.service.FriendService;
import swypraven.complimentlabserver.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<ApiResponse<ResponseCreateFriend>> create(@RequestBody RequestCreateFriend friend) {
        ResponseCreateFriend character = friendService.create(friend);
        return ResponseEntity.status(201).body(ApiResponse.success(character, "201", "성공"));
    }
}
