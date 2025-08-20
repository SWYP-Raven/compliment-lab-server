package swypraven.complimentlabserver.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.user.model.request.AppleLoginRequest;
import swypraven.complimentlabserver.domain.user.model.request.TokenRefreshRequest;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.domain.user.service.AppleAuthService;
import swypraven.complimentlabserver.domain.user.service.TokenRefreshService;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    @Autowired(required = false)  // Optional 주입
    private final AppleAuthService appleAuthService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/apple/login")
    public ResponseEntity<ApiResponse<AppleLoginResponse>> appleLogin(
            @Valid @RequestBody AppleLoginRequest request) throws ParseException {

        if (appleAuthService == null) {
            throw new RuntimeException("Apple 로그인이 비활성화되어 있습니다.");
        }

        AppleLoginResponse response = appleAuthService.appleLogin(request.identityToken());
        return ResponseEntity.ok(ApiResponse.of(true, response, "애플 로그인 성공"));
    }
    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 이용한 Access Token 재발급")
    public ResponseEntity<ApiResponse<JwtToken>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        JwtToken newToken = tokenRefreshService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.of(true, newToken, "토큰 갱신 성공"));
    }
}
