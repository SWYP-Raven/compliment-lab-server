package swypraven.complimentlabserver.domain.user.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.request.AppleLoginRequest;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.domain.user.service.AppleAuthService;
import swypraven.complimentlabserver.domain.user.service.UserService;
import swypraven.complimentlabserver.domain.user.service.AppleIdTokenValidator;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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