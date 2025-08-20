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
import swypraven.complimentlabserver.domain.user.repository.RefreshTokenRepository;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Transactional
    public JwtToken refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new LoginFailedException.InvalidJwtTokenException("유효하지 않은 Refresh Token입니다.");
        }

        String userEmail = refreshTokenRepository.findUserEmailByToken(refreshToken)
                .orElseThrow(() -> new LoginFailedException.InvalidJwtTokenException("존재하지 않는 Refresh Token입니다.")).toString();

        User user = userService.findOrCreateByEmail(userEmail);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );

        JwtToken newJwtToken = jwtTokenProvider.generateToken(authentication);

        refreshTokenRepository.deleteByToken(refreshToken);
        refreshTokenRepository.save(userEmail, newJwtToken.getRefreshToken());

        log.info("토큰 갱신 완료: user={}", userEmail);

        return newJwtToken;
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
        log.info("Refresh Token 무효화 완료");
    }

    @Transactional
    public void invalidateAllRefreshTokens(String userEmail) {
        refreshTokenRepository.deleteAllByUserEmail(userEmail);
        log.info("사용자의 모든 Refresh Token 무효화 완료: user={}", userEmail);
    }
}
