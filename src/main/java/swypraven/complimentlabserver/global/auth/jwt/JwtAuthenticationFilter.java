package swypraven.complimentlabserver.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import swypraven.complimentlabserver.global.exception.auth.AuthErrorCode;
import swypraven.complimentlabserver.global.exception.auth.AuthException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 문자열 기반 화이트리스트 (와일드카드 포함 가능)
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/auth/apple/login",
            "/auth/apple/signup",
            "/auth/token/refresh",
            "/actuator/",
            "/swagger-ui/",
            "/v3/api-docs",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        final String uri = request.getRequestURI();

        // 0) CORS Preflight 무조건 통과
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) 화이트리스트 경로면 JWT 검사 스킵
        if (shouldSkipFilter(uri)) {
            log.debug("[JWT] Skipped by whitelist: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2) 토큰 추출
            final String token = resolveToken(request);

            // 2-1) 토큰 없으면 통과 (익명으로 처리 → 이후 인가 단계에서 거름)
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2-2) 토큰이 있는데 유효하지 않으면 예외 → EntryPoint로 위임 (401)
            if (!jwtTokenProvider.validateToken(token)) {
                throw new LoginFailedException.InvalidJwtTokenException("Invalid or expired token");
            }

        } catch (Exception e) {
            log.warn("JWT 처리 중 예외 발생: {}", e.getMessage());
            // response.write 대신 예외를 던져서 EntryPoint로 위임
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        filterChain.doFilter(request, response);
    }

    /** 화이트리스트 매칭 */
    private boolean shouldSkipFilter(String uri) {
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /** Authorization 헤더에서 Bearer 토큰 추출 */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
