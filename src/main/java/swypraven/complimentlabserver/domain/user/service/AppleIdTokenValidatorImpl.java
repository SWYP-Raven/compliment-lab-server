package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.AuthException;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "apple", name = "stub", havingValue = "false", matchIfMissing = true)
public class AppleIdTokenValidatorImpl implements AppleIdTokenValidator {

    private static final String JWK_URL = "https://appleid.apple.com/auth/keys";
    private static final String ISS = "https://appleid.apple.com";
    private static final long CLOCK_SKEW_MS = 60_000L;

    @Value("${apple.client-id}")
    private String appleClientId;

    @Override
    public JWTClaimsSet validate(String idToken) {
        try {
            SignedJWT signed = SignedJWT.parse(idToken);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = createJwtProcessor();
            JWTClaimsSet claims = jwtProcessor.process(signed, null);

            if (!ISS.equals(claims.getIssuer())) {
                throw new AuthException(AuthErrorCode.JWT_SIGNATURE_INVALID);
            }
            List<String> aud = claims.getAudience();
            if (aud == null || !aud.contains(appleClientId)) {
                throw new AuthException(AuthErrorCode.JWT_SIGNATURE_INVALID);
            }
           // 만료 시간 검증
            Date exp = claims.getExpirationTime();
            if (exp == null || new Date(System.currentTimeMillis() - CLOCK_SKEW_MS).after(exp)) {
                throw new AuthException(AuthErrorCode.JWT_TOKEN_EXPIRED);
            }

            return claims;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple ID Token 파싱/검증 실패", e);
            throw new AuthException(AuthErrorCode.APPLE_AUTH_FAILED);
        }
    }


    ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() throws Exception {
        RemoteJWKSet<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(JWK_URL));
        JWSVerificationKeySelector<SecurityContext> selector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        DefaultJWTProcessor<SecurityContext> p = new DefaultJWTProcessor<>();
        p.setJWSKeySelector(selector);
        p.setJWTClaimsSetVerifier((claims, context) -> { /* 수동 검증은 위에서 */ });
        return p;
    }
}