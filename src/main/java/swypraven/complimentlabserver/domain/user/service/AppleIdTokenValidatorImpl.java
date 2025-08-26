// src/main/java/.../domain/user/service/AppleIdTokenValidatorImpl.java
package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

import java.net.URL;

@Slf4j
@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        value = "apple.stub",
        havingValue = "false",
        matchIfMissing = true // 기본은 실제 구현
)
public class AppleIdTokenValidatorImpl implements AppleIdTokenValidator {

    private static final String JWK_URL = "https://appleid.apple.com/auth/keys";
    private static final String ISS = "https://appleid.apple.com";

    @Value("${apple.client-id}")
    private String appleClientId;

    @Override
    public JWTClaimsSet validate(String idToken) {
        try {
            SignedJWT signed = SignedJWT.parse(idToken);

            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = createJwtProcessor();
            JWTClaimsSet claims = jwtProcessor.process(signed, null);

            if (!ISS.equals(claims.getIssuer())) {
                throw new LoginFailedException.AppleIdTokenValidationException("iss 불일치");
            }
            if (!claims.getAudience().contains(appleClientId)) {
                throw new LoginFailedException.AppleIdTokenValidationException("aud 불일치");
            }
//             //만료 체크를 켜고 싶으면 주석 해제
//             Date exp = claims.getExpirationTime();
//             if (exp == null || exp.before(new Date())) {
//                 throw new LoginFailedException.AppleIdTokenValidationException("토큰 만료");
//             }

            return claims;

        } catch (LoginFailedException.AppleIdTokenValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple ID Token 파싱/검증 실패", e);
            throw new LoginFailedException.AppleIdTokenValidationException(
                    "Apple ID Token 파싱/검증 실패: " + e.getMessage(), e);
        }
    }

    // RS256 서명 검증용 JWKS 셋업 (캐싱 포함)
    ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() throws Exception {
        var jwkSource = new com.nimbusds.jose.jwk.source.RemoteJWKSet<SecurityContext>(new URL(JWK_URL));
        var selector  = new com.nimbusds.jose.proc.JWSVerificationKeySelector<SecurityContext>(
                com.nimbusds.jose.JWSAlgorithm.RS256, jwkSource);
        var p = new com.nimbusds.jwt.proc.DefaultJWTProcessor<SecurityContext>();
        p.setJWSKeySelector(selector);
        p.setJWTClaimsSetVerifier((claims, context) -> { /* 수동 검증은 위에서 */ });
        return p;
    }
}
