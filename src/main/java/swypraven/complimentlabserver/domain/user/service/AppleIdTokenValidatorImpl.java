// src/main/java/.../domain/user/service/AppleIdTokenValidatorImpl.java
package swypraven.complimentlabserver.domain.user.service;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.AuthException;
import java.net.URL;


@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
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
            if (!ISS.equals(claims.getIssuer()) || !claims.getAudience().contains(appleClientId)) {
                throw new AuthException(AuthErrorCode.JWT_SIGNATURE_INVALID);
            }
             //만료 체크를 켜고 싶으면 주석 해제
             //Date exp = claims.getExpirationTime();
             //if (exp == null || exp.before(new Date())) {
                 //throw new AuthException(AuthErrorCode.APPLE_TOKEN_EXPIRED);
            // }

            return claims;

        }
        catch (AuthException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Apple ID Token 파싱/검증 실패", e);
            throw new AuthException(AuthErrorCode.APPLE_AUTH_FAILED);
        }
    }

    // RS256 서명 검증용 JWKS 셋업 (캐싱 포함)
    ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() throws Exception {
        RemoteJWKSet<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(JWK_URL));
        // 변경: RS256으로 설정해야 Apple의 공개키 검증이 정상 동작
        JWSVerificationKeySelector<SecurityContext> selector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        DefaultJWTProcessor<SecurityContext> p = new DefaultJWTProcessor<>();
        p.setJWSKeySelector(selector);
        p.setJWTClaimsSetVerifier((claims, context) -> { /* 수동 검증은 위에서 */ });
        return p;
    }
}