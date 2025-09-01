package swypraven.complimentlabserver.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.user.model.request.NicknameRequest;
import swypraven.complimentlabserver.domain.user.service.UserService;

import swypraven.complimentlabserver.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PutMapping("/nickname")
    public ResponseEntity<ApiResponse<?>> setNickname(@RequestBody NicknameRequest nicknameRequest) {
        userService.setNickname(nicknameRequest);
        return ResponseEntity.ok(ApiResponse.success("200", "닉네임 설정 성공"));
    }
}
