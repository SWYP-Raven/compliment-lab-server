package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

@Service
public class AppleIdTokenValidator {
    //토큰 검증 전담
    @Value("${apple.client.id}")
    private String appleClientId;

    private final AppleAuthService appleAuthService;

    public AppleIdTokenValidator(AppleAuthService appleAuthService) {
        this.appleAuthService = appleAuthService;
    }

    public JWTClaimsSet validate(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = appleAuthService.getJwtProcessor();
            JWTClaimsSet claimsSet = jwtProcessor.process(signedJWT, null);

            if (!"https://appleid.apple.com".equals(claimsSet.getIssuer())
                    || !claimsSet.getAudience().contains(appleClientId)) {
                throw new LoginFailedException.AppleIdTokenValidationException("ID 토큰 검증 실패: iss/aud 불일치");
            }

            return claimsSet;
        } catch (LoginFailedException.AppleIdTokenValidationException e) {
            throw e; // 이미 커스텀 예외
        } catch (Exception e) {
            throw new LoginFailedException.AppleIdTokenValidationException("Apple ID Token 파싱/검증 실패: " + e.getMessage());
        }
    }
}