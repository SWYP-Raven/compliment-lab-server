package swypraven.complimentlabserver.global.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private final UserRepository userRepository;
    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey; // Base64 인코딩된 256bit 이상 키 권장

    @Value("${jwt.access-token.expiry:1800}")   // seconds
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

    /**
     * Access + Refresh Token 생성
     * subject는 Authentication.getName() (현재 appleSub) 사용
     * Access 토큰에는 권한(auth) + 선택 클레임(uid,email,role) 포함
     */
    public JwtToken generateToken(Authentication authentication,
                                  User user) {

        final long now = System.currentTimeMillis();
        final String subject = Optional.ofNullable(authentication.getName()).orElse("");

        // 권한 문자열 (예: "ROLE_USER,ROLE_ADMIN")
        final String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(","));

        // 선택: 프런트/게이트웨이에서 쓰기 좋은 클레임들
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("auth", authorities);
        if (user != null) {
            if (user.getId() != null)    extraClaims.put("uid", user.getId());
            if (user.getEmail() != null) extraClaims.put("email", user.getEmail());
            if (user.getRole() != null)  extraClaims.put("role", user.getRole());
        }

        String accessToken  = buildAccessToken(subject, now, extraClaims);
        String refreshToken = buildRefreshToken(subject, now);

        log.info("JWT 토큰 생성 완료: user={}", subject);
        log.info("JWT 토큰 생성 완료: accessToken={}, refreshToken={}", accessToken, refreshToken);

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

        // 기본 헤더 typ=JWT는 자동, 필요한 경우 kid 등 추가 가능
        // builder.setHeaderParam("kid", "your-key-id");

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

    /**
     * Access 토큰에서 Authentication 생성
     * - auth 클레임이 없으면 Access 토큰이 아니라고 판단
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String authClaim = Optional.ofNullable(claims.get("auth"))
                .map(Object::toString)
                .orElse(null);

        if (authClaim == null || authClaim.isBlank()) {
            // Access 토큰이 아니거나 권한 정보가 없는 경우
            throw new AuthException(AuthErrorCode.JWT_TOKEN_INVALID);
        }

        List<? extends GrantedAuthority> authorities =
                Arrays.stream(authClaim.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .toList();


        String appleSub = claims.getSubject();
        User user = userRepository.findByAppleSub(appleSub)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));


        CustomUserDetails principal = new CustomUserDetails(user, authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
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

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
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
}
