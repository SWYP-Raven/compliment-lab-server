package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppleIdTokenValidator {

    @Value("${apple.client.id}")
    private String appleClientId;

    private final AppleAuthService appleAuthService;

    public AppleIdTokenValidator(AppleAuthService appleAuthService) {
        this.appleAuthService = appleAuthService;
    }

    public JWTClaimsSet validate(String idToken) {
        try {
            // 1. ID 토큰 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // 2. 애플 공개키를 이용한 검증
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = appleAuthService.getJwtProcessor();
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
}
