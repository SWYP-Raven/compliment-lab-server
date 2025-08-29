package swypraven.complimentlabserver.global.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.AuthException;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Getter
    private final UserRepository userRepository;
    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey; // Base64 인코딩된 256bit 이상 키 권장

    @Value("${jwt.access-token.expiry:1800}")    // seconds
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token.expiry:604800}") // seconds (7d)
    private long refreshTokenExpiry;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            accessTokenExpiry  *= 1000; // sec -> ms
            refreshTokenExpiry *= 1000; // sec -> ms
        } catch (Exception e) {
            log.error("JWT Secret Key 초기화 실패", e);
            throw new IllegalStateException("JWT Secret Key가 올바르지 않습니다.");
        }
    }
  
    /* ------------------------------------------------------------------
     * 토큰 생성 (단건: 액세스)
     *  - 클레임: userId, role, auth(콤마 구분)
     *  - subject는 필요에 따라 userId 또는 외부 식별자(appleSub) 사용 가능
     * ------------------------------------------------------------------ */
    public String createAccessToken(Long userId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpiry);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // 필요시 appleSub로 교체 가능
                .claim("userId", userId)              // ✅ 통일: userId
                .claim("role", role)
                .claim("auth", role)                  // 다중 권한이면 콤마구분 "A,B"
                .setIssuedAt(now).setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ------------------------------------------------------------------
     * 토큰 생성 (액세스 + 리프레시 묶음)
     *  - generateToken(authentication, user) 기존 시그니처 유지
     *  - 액세스 토큰 클레임은 위 createAccessToken과 동일하게 userId 사용
     * ------------------------------------------------------------------ */
    public JwtToken generateToken(Authentication authentication,
                                  User user) {

        final long now = System.currentTimeMillis();
        final String subject = Optional.ofNullable(authentication.getName()).orElse("");

        // 권한 문자열 (예: "ROLE_USER,ROLE_ADMIN")
        final String authString = authentication != null
                ? authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(","))
                : (user != null && user.getRole() != null ? user.getRole() : "");

        Map<String, Object> extraClaims = new HashMap<>();
        if (!authString.isBlank()) extraClaims.put("auth", authString);

        if (user != null) {
            if (user.getId() != null)    extraClaims.put("userId", user.getId()); // ✅ uid -> userId
            if (user.getRole() != null)  extraClaims.put("role", user.getRole());
            if (user.getEmail() != null) extraClaims.put("email", user.getEmail()); // 민감도 고려해 필요시 제거
        }

        String accessToken  = buildAccessToken(subject, now, extraClaims);
        String refreshToken = buildRefreshToken(subject, now);

        log.info("JWT 토큰 생성 완료: subject={}, userId={}", subject, extraClaims.get("userId"));
        // ⚠️ 전체 토큰 문자열은 로그에 남기지 마세요.

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String buildAccessToken(String subject, long now, Map<String, Object> claims) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpiry))
                .signWith(key, SignatureAlgorithm.HS256);

        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }
        return builder.compact();
    }

    private String buildRefreshToken(String subject, long now) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("typ", "refresh") // 리프레시 토큰 식별
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ------------------------------------------------------------------
     * 인증 객체 복원
     *  - 필수: 권한(auth 또는 role)
     *  - userId 우선, 없으면 uid(구토큰 호환), 그래도 없으면 subject를 Long으로 시도
     *  - principal: CustomUserPrincipal(userId, subject, role)
     * ------------------------------------------------------------------ */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) throws InvalidJwtTokenException {
        Claims c = parseClaims(token);

        // 권한
        String auth = Optional.ofNullable(c.get("auth")).map(Object::toString).orElse("");
        String role = Optional.ofNullable(c.get("role")).map(Object::toString).orElse("");
        List<SimpleGrantedAuthority> authorities =
                !auth.isBlank()
                        ? Arrays.stream(auth.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty())
                        .map(SimpleGrantedAuthority::new).toList()
                        : (role.isBlank() ? List.of() : List.of(new SimpleGrantedAuthority(role)));

        if (authorities.isEmpty()) {
            throw new InvalidJwtTokenException("권한 정보가 없는 토큰입니다.");
        }

        // userId 추출(우선순위: userId -> uid(호환) -> subject Long)
        Long userId = extractLong(c, "userId");
        if (userId == null) userId = extractLong(c, "uid");
        if (userId == null) {
            try { userId = Long.valueOf(c.getSubject()); }
            catch (Exception e) { throw new InvalidJwtTokenException("userId 클레임 없음"); }
        }

        String subject = c.getSubject(); // appleSub 등 외부 식별자 보관용
        String finalRole = !role.isBlank() ? role : authorities.get(0).getAuthority();

        var principal = new CustomUserPrincipal(userId, subject, finalRole);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Long extractLong(Claims c, String key) {
        Object v = c.get(key);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try { return Long.valueOf(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.JWT_TOKEN_INVALID);
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.JWT_TOKEN_INVALID);
        }
    }


    public Date getExpirationDateFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date exp = getExpirationDateFromToken(token);
        return exp != null && exp.before(new Date());
    }

    public boolean isRefreshToken(String refreshToken) {
        return "refresh".equals(parseClaims(refreshToken).get("typ", String.class));
    }

    private class InvalidJwtTokenException extends Throwable {
        public InvalidJwtTokenException(String s) {
        }
    }
}
