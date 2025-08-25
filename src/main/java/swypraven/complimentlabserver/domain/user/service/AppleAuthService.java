package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleIdTokenValidator appleIdTokenValidator; // 서명/클레임 검증(런타임 예외로 래핑)
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 애플 로그인 플로우:
     * 1) idToken 검증(서명 + iss/aud 등)
     * 2) 사용자 조회/생성
     * 3) Access/Refresh 발급
     * (체크 예외는 모두 validator 내부에서 런타임으로 래핑됨)
     */
    public AppleLoginResponse appleLogin(String idToken) throws ParseException {
        // 1) 토큰 검증 (유효하지 않으면 LoginFailedException.* 런타임 예외 발생)
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);

        // 2) 사용자 조회/생성 (애플은 최초 로그인 때만 email 제공될 수 있음)
        String email = claims.getStringClaim("email"); // null 가능
        String sub = claims.getSubject();              // 애플 고유 사용자 ID(고정)
        User user = userService.findByEmail("yus174113@gmail.com").get();

        // 3) JWT 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getAppleSub(), // 또는 userId
                null,
                List.of(new SimpleGrantedAuthority(user.getRole())) // ROLE_USER
        );
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication, user); // user 넘겨서 email/role 클레임 포함


        return AppleLoginResponse.builder()
                .grantType(jwtToken.grantType())   // 보통 "Bearer"
                .accessToken(jwtToken.accessToken())
                .refreshToken(jwtToken.refreshToken())
                .build();
    }
}
