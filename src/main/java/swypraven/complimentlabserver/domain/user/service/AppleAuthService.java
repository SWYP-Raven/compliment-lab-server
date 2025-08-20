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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;
import swypraven.complimentlabserver.domain.user.model.response.AppleLoginResponse;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.net.URL;
import java.text.ParseException;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthService {
    private final String appleAuthUrl = "https://appleid.apple.com/auth/keys";
    private final AppleIdTokenValidator appleIdTokenValidator;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public ConfigurableJWTProcessor<SecurityContext> getJwtProcessor() {
        try {
            JWKSet jwkSet = JWKSet.load(new URL(appleAuthUrl));
            JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);

            return jwtProcessor;
        } catch (Exception e) {
            throw new LoginFailedException("Apple 공개키 로드 실패: " + e.getMessage());
        }
    }

    public AppleLoginResponse appleLogin(String idToken) throws ParseException {
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String email = claims.getStringClaim("email");
        String sub = claims.getSubject();

        User user = userService.findOrCreateByAppleSub(sub, email);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        JwtToken jwtToken = jwtTokenProvider.generateToken(auth);

        return AppleLoginResponse.builder()
                .grantType(jwtToken.getGrantType())
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }
}

