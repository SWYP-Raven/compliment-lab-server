package swypraven.complimentlabserver.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.user.model.request.UpdateUserRequest;
import swypraven.complimentlabserver.domain.user.model.response.UserInfoResponse;
import swypraven.complimentlabserver.domain.user.service.UserService;

import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;
import swypraven.complimentlabserver.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<ApiResponse<UserInfoResponse>> setNickname(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal CustomUserPrincipal me)
    {
        UserInfoResponse userInfoResponse = userService.updateUser(request, me.id());
        return ResponseEntity.ok(ApiResponse.success(userInfoResponse, "200", "유저 업데이트"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@AuthenticationPrincipal CustomUserPrincipal me) {
        UserInfoResponse userInfo = userService.getUserInfo(me.id());
        return ResponseEntity.ok(ApiResponse.success(userInfo, "200","조회 성공"));
    }
}