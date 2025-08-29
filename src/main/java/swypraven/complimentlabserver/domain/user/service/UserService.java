// src/main/java/swypraven/complimentlabserver/domain/user/service/UserService.java
package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.dto.FindOrCreateAppleUserDto;
import swypraven.complimentlabserver.domain.user.model.request.NicknameRequest;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AppleIdTokenValidator appleIdTokenValidator;


    /**
     * Apple sub(고유 ID) 기준으로 조회하고 없으면 생성
     * email은 첫 로그인에만 제공될 수 있으니 null 허용, 업데이트 가능하게 처리
     */
    @Transactional
    public FindOrCreateAppleUserDto findOrCreateByAppleSub(String sub, String email) {
        return userRepository.findByAppleSub(sub)
                .map(user -> {
                    if(user.getNickname() == null || user.getNickname().isEmpty()) {
                        return new FindOrCreateAppleUserDto(user, false);
                    }
                    return new FindOrCreateAppleUserDto(user, true);
                })
                .orElseGet(() -> {
                    User newUser = userRepository.save(new User(email, sub).setRole("ROLE_USER"));
                    return new FindOrCreateAppleUserDto(newUser, false);
                });
    }

    public User getByAppleSub(String appleSub) {
        return userRepository.findByAppleSub(appleSub)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by appleSub: " + appleSub));
    }


    public Optional<User> findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    /**
     * Spring Security 표준: username으로 사용자 로드
     * 여기서는 username = appleSub
     */
    @Override
    public UserDetails loadUserByUsername(String appleSub) throws UsernameNotFoundException {
        User user = getByAppleSub(appleSub);
        String role = (user.getRole() == null || user.getRole().isBlank())
                ? "ROLE_USER"
                : (user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole());
        List<SimpleGrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new CustomUserDetails(user, auth);
    }

    /** 토큰에 userId를 넣는 전략일 때 유용한 헬퍼 */
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by id: " + userId));
        String role = (user.getRole() == null || user.getRole().isBlank())
                ? "ROLE_USER"
                : (user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole());
        List<SimpleGrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new CustomUserDetails(user, auth);
    }


    @Transactional
    public void setNickname(NicknameRequest request) {
        JWTClaimsSet claims = appleIdTokenValidator.validate(request.identityToken());
        String sub = claims.getSubject();
        User user = userRepository.findByAppleSub(sub).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        user.setNickname(request.nickname());
    }
}
