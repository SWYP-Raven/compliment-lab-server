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

    // Bean 생성 후 키 초기화
    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            // 밀리초 단위로 변환
            accessTokenExpiry = accessTokenExpiry * 1000;
            refreshTokenExpiry = refreshTokenExpiry * 1000;
        } catch (Exception e) {
            log.error("JWT Secret Key 초기화 실패", e);
            throw new IllegalStateException("JWT Secret Key가 올바르지 않습니다.");
        }
    }

    // Access + Refresh Token 생성
    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        String accessToken = createToken(authentication.getName(), authorities, now + accessTokenExpiry);
        String refreshToken = createToken(null, null, now + refreshTokenExpiry);

        log.debug("JWT 토큰 생성 완료: user={}", authentication.getName());

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String createToken(String subject, String authorities, long expirationMillis) {
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256);

        if (subject != null) builder.setSubject(subject);
        if (authorities != null) builder.claim("auth", authorities);

        return builder.compact();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = parseClaims(token);

        Object authClaim = claims.get("auth");
        if (authClaim == null) {
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
}
