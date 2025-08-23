package swypraven.complimentlabserver.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.global.exception.auth.LoginFailedException;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Transactional
    public JwtToken refreshToken(String refreshToken){
        // 문법/서명/만료 검증 (실패 시 예외 발생)
        jwtTokenProvider.validateToken(refreshToken);

        // 선택: typ=refresh 확인
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new LoginFailedException.InvalidJwtTokenException("Refresh Token이 아닙니다.");
        }

        User user = userService.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new LoginFailedException.InvalidJwtTokenException("존재하지 않는 Refresh Token입니다."));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), // 가능하면 appleSub 또는 userId로 바꾸는 게 안전
                null,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );

        JwtToken newJwtToken = jwtTokenProvider.generateToken(authentication, user);

        // 새 RT 저장
        user.setRefreshToken(newJwtToken.refreshToken());

        log.info("토큰 갱신 완료: user={}", user.getEmail());
        return newJwtToken;
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        userService.findByRefreshToken(refreshToken)
                .ifPresent(user -> user.setRefreshToken(null));

        log.info("Refresh Token 무효화 완료");
    }

}

