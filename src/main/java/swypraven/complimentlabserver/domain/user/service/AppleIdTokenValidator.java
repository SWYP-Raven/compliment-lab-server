package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

@Service
public class AppleIdTokenValidator {

    @Value("${apple.client-id}")
    private String appleClientId;

    public JWTClaimsSet validate(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            // (예시) 실 운영에선 JWK 설정까지 해서 서명 검증 필수
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = createJwtProcessor();
            JWTClaimsSet claimsSet = jwtProcessor.process(signedJWT, null);

            // 표준 클레임 검증
            if (!"https://appleid.apple.com".equals(claimsSet.getIssuer())
                    || !claimsSet.getAudience().contains(appleClientId)) {
                throw new LoginFailedException.AppleIdTokenValidationException("ID 토큰 검증 실패: iss/aud 불일치");
            }
            return claimsSet;

        } catch (Exception e) {
            // 우리가 던진 커스텀 예외면 그대로
            if (e instanceof LoginFailedException.AppleIdTokenValidationException) {
                throw (LoginFailedException.AppleIdTokenValidationException) e;
            }
            // 그 외 체크 예외( ParseException / BadJOSEException / JOSEException / MalformedURLException )는 런타임으로 래핑
            throw new LoginFailedException.AppleIdTokenValidationException(
                    "Apple ID Token 파싱/검증 실패: " + e.getMessage(), e
            );
        }
    }

    ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() {
        return new DefaultJWTProcessor<>();
    }
}
