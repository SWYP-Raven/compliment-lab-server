package swypraven.complimentlabserver.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import swypraven.complimentlabserver.domain.user.model.request.AppleLoginRequest;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.domain.user.service.AppleAuthService;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;

import java.text.ParseException;

//@RestController
//@RequestMapping("/auth")
@RequiredArgsConstructor
public class AppleAuthController {
    private final AppleAuthService appleAuthService;

    @PostMapping("/apple")
    public AppleLoginResponse appleLogin(@RequestBody AppleLoginRequest request) throws ParseException {
        JwtToken jwtToken = appleAuthService.appleLogin(request.identityToken());
        return AppleLoginResponse.builder()
                .grantType(jwtToken.getGrantType())
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }
}