package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

import java.net.URL;
import java.util.Date;

import static org.springframework.security.oauth2.jwt.JwtClaimNames.ISS;

@Service
public class AppleIdTokenValidator {

    private static final String JWK_URL = ISS + "/auth/keys";
    ;
    @Value("${apple.client-id}")
    private String appleClientId;

    public JWTClaimsSet validate(String idToken) {
        try {
            SignedJWT signed = SignedJWT.parse(idToken);

            // 1) 서명 검증
            var jwtProcessor = createJwtProcessor();
            JWTClaimsSet claims = jwtProcessor.process(signed, null);

            // 2) 표준 클레임 검증
            if (!ISS.equals(claims.getIssuer())) {
                throw new LoginFailedException.AppleIdTokenValidationException("iss 불일치");
            }
            if (!claims.getAudience().contains(appleClientId)) {
                throw new LoginFailedException.AppleIdTokenValidationException("aud 불일치");
            }
            // (선택) 만료/발급시각/nonce 등 추가검증
            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())) {
                throw new LoginFailedException.AppleIdTokenValidationException("토큰 만료");
            }

            return claims;

        } catch (LoginFailedException.AppleIdTokenValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new LoginFailedException.AppleIdTokenValidationException("Apple ID Token 파싱/검증 실패: " + e.getMessage(), e);
        }
    }

    // ⬇️ RS256 서명 검증용 JWKS 셋업 (캐싱 포함)
    ConfigurableJWTProcessor<SecurityContext> createJwtProcessor() throws Exception {
        var jwkSource = new com.nimbusds.jose.jwk.source.RemoteJWKSet<SecurityContext>(new URL(JWK_URL));
        var selector = new com.nimbusds.jose.proc.JWSVerificationKeySelector<SecurityContext>(
                com.nimbusds.jose.JWSAlgorithm.RS256, jwkSource);
        var p = new com.nimbusds.jwt.proc.DefaultJWTProcessor<SecurityContext>();
        p.setJWSKeySelector(selector);
        // (선택) clock skew 허용
        p.setJWTClaimsSetVerifier((claims, context) -> { /* no-op, 수동검증은 위에서 */ });
        return p;
    }
}
