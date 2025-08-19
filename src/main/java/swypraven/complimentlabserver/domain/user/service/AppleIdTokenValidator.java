package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class AppleIdTokenValidator {

    @Value("${apple.client.id}")
    private String appleClientId;

    private final String appleAuthUrl = "https://appleid.apple.com/auth/keys";

//    private final AppleAuthService appleAuthService;
//
//    public AppleIdTokenValidator(AppleAuthService appleAuthService) {
//        this.appleAuthService = appleAuthService;
//    }

    public JWTClaimsSet validate(String idToken) {
        try {
            // 1. ID 토큰 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // 2. 애플 공개키를 이용한 검증
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = getJwtProcessor();
//            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor();
            JWTClaimsSet claimsSet = jwtProcessor.process(signedJWT, null);

            // 3. claims 검증 (iss, aud)
            if (!"https://appleid.apple.com".equals(claimsSet.getIssuer())
                    || !claimsSet.getAudience().contains(appleClientId)) {
                throw new IllegalStateException("ID 토큰 검증 실패");
            }

            return claimsSet;
        } catch (Exception e) {
            throw new RuntimeException("Apple ID Token 검증 실패", e);
        }
    }
    private ConfigurableJWTProcessor<SecurityContext> getJwtProcessor() {
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
}
