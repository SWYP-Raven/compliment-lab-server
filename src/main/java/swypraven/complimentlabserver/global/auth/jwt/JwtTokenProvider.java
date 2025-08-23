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
import org.springframework.security.core.userdetails.User;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access-token.expiry:1800}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh-token.expiry:604800}")
    private long refreshTokenExpiry;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            accessTokenExpiry *= 1000;   // sec -> ms
            refreshTokenExpiry *= 1000;  // sec -> ms
        } catch (Exception e) {
            log.error("JWT Secret Key 초기화 실패", e);
            throw new IllegalStateException("JWT Secret Key가 올바르지 않습니다.");
        }
    }

    // Access + Refresh Token 생성
    public JwtToken generateToken(Authentication authentication,
                                  swypraven.complimentlabserver.domain.user.entity.User user) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        String subject = authentication.getName(); // 필요시 appleSub 등 안정 식별자로 교체

        // Access 토큰: sub + auth
        String accessToken = Jwts.builder()
                .setSubject(subject)
                .claim("auth", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh 토큰: sub + typ=refresh
        String refreshToken = Jwts.builder()
                .setSubject(subject)
                .claim("typ", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.debug("JWT 토큰 생성 완료: user={}", subject);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)   // ✅ 여기 fix
                .build();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Object authClaim = claims.get("auth");
        if (authClaim == null) {
            // Access 토큰이 아니거나 권한 정보가 없는 경우
            throw new LoginFailedException.InvalidJwtTokenException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(authClaim.toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new LoginFailedException.InvalidJwtTokenException("유효하지 않은 JWT 토큰입니다: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) return false;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new LoginFailedException.InvalidJwtTokenException("유효하지 않은 JWT 토큰입니다: " + e.getMessage(), e);
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // ✅ 미구현 메서드 구현
    public boolean isRefreshToken(String refreshToken) {
        return "refresh".equals(parseClaims(refreshToken).get("typ", String.class));
    }
}
