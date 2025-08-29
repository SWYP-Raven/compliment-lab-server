package swypraven.complimentlabserver.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.user.model.request.AppleLoginRequest;
import swypraven.complimentlabserver.domain.user.model.request.TokenRefreshRequest;
import swypraven.complimentlabserver.domain.user.model.response.AppleAuthResponse;
import swypraven.complimentlabserver.domain.user.service.AppleAuthService;
import swypraven.complimentlabserver.domain.user.service.TokenRefreshService;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.response.ApiResponse;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    private final AppleAuthService appleAuthService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping(value = "/apple", consumes = "application/json", produces = "application/json")
    @Operation(summary = "애플 로그인", description = "가입된 사용자만 로그인 및 토큰 발급")
    public ResponseEntity<ApiResponse<AppleAuthResponse>> appleLogin(
            @Valid @RequestBody AppleLoginRequest request
    ) throws ParseException {
        AppleAuthResponse response = appleAuthService.appleLogin(request.identityToken());
        return ResponseEntity.ok(ApiResponse.of(true, response, "애플 로그인 성공"));
    }

    @PostMapping("/nickname")
    public ResponseEntity<?> appleLogin(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getId());
        appleAuthService.setNickname();
        return ResponseEntity.ok(null);
    }


    @PostMapping(value = "/token/refresh", consumes = "application/json", produces = "application/json")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 Access/Refresh 재발급")
    public ResponseEntity<ApiResponse<JwtToken>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        JwtToken newToken = tokenRefreshService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.of(true, newToken, "토큰 갱신 성공"));
    }
}
