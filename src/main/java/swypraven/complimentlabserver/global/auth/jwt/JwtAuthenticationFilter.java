package swypraven.complimentlabserver.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // JwtAuthenticationFilter
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/v1/auth/apple/login",
            "/api/v1/auth/apple/signup",
            "/api/v1/auth/token/refresh",
            "/actuator/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/favicon.ico"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        try {
            if (shouldSkipFilter(requestURI)) {
                log.debug("JWT 검증 제외 경로: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }

            String token = resolveToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT 인증 성공: user={}, uri={}", authentication.getName(), requestURI);
            }

        } catch (LoginFailedException.InvalidJwtTokenException e) {
            log.warn("JWT 처리 중 예외 발생: {}", e.getMessage());
            // response.write 대신 예외를 던져서 EntryPoint로 위임
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkipFilter(String requestURI) {
        return EXCLUDED_PATHS.stream().anyMatch(requestURI::startsWith);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }
}
