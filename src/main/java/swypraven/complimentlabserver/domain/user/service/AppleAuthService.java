package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.AuthException;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleIdTokenValidator appleIdTokenValidator;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인: 존재하는 사용자만 허용
    public AppleLoginResponse appleLogin(String idToken) throws ParseException {
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String sub = claims.getSubject();
        String email = claims.getStringClaim("email"); // null 가능

        // 존재 여부 체크
        User user = userService.findByAppleSubOptional(sub)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NONE_EXIST_USER));

        // 이메일이 새로 들어왔다면(최초 이후), 비어있는 경우에만 업데이트
        if (user.getEmail() == null && email != null) {
            user.setEmail(email);
        }

        JwtToken token = issue(user);
        return toLoginResponse(token, user);

    }

    // 회원가입: 닉네임까지 받아 새 사용자 생성 후 토큰 발급
    public AppleLoginResponse appleSignup(String idToken, String nickname) throws ParseException {
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String sub = claims.getSubject();
        String email = claims.getStringClaim("email"); // null 가능

        if (userService.existsByAppleSub(sub)) {
            throw new AuthException(AuthErrorCode.EXIST_USER);
        }

        // 새 사용자 생성 (닉네임 필수)
        User user = userService.createUserWithApple(sub, email, nickname);

        // ✅ 수정
        JwtToken token = issue(user);
        return toLoginResponse(token, user);

    }

    private JwtToken issue(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getAppleSub(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
        return jwtTokenProvider.generateToken(authentication, user);
    }

    private AppleLoginResponse toLoginResponse(JwtToken jwtToken, User user) {
        return AppleLoginResponse.builder()
                .grantType(jwtToken.grantType())
                .accessToken(jwtToken.accessToken())
                .refreshToken(jwtToken.refreshToken())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }

}
