package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.net.URL;
import java.text.ParseException;
import java.util.List;

//@Service
@RequiredArgsConstructor // 의존성 주입을 위한 Lombok 어노테이션
public class AppleAuthService {

    private final String appleAuthUrl = "https://appleid.apple.com/auth/keys";
    private final AppleIdTokenValidator appleIdTokenValidator; // 의존성 주입
    private final UserService userService; // 의존성 주입
    private final JwtTokenProvider jwtTokenProvider; // 의존성 주입

    public ConfigurableJWTProcessor<SecurityContext> getJwtProcessor() {
        try {
            JWKSet jwkSet = JWKSet.load(new URL(appleAuthUrl));
            JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

            JWSKeySelector<SecurityContext> keySelector =
                    new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);

            return jwtProcessor;
        } catch (Exception e) {
            throw new RuntimeException("Apple 공개키 로드 실패", e);
        }
    }

    public JwtToken appleLogin(String idToken) throws ParseException {
        // 1. Apple ID Token 유효성 검증
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String email = claims.getStringClaim("email");
        String sub = claims.getSubject();

        // 2. 유저 생성 또는 조회
        User user = userService.findOrCreateByAppleSub(sub, email);

        // 3. Authentication 객체 생성 후 JWT 발급
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return jwtTokenProvider.generateToken(auth);
    }
}