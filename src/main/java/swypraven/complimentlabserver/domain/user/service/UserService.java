// src/main/java/swypraven/complimentlabserver/domain/user/service/UserService.java
package swypraven.complimentlabserver.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Apple sub(고유 ID) 기준으로 조회하고 없으면 생성
     * email은 첫 로그인에만 제공될 수 있으니 null 허용, 업데이트 가능하게 처리
     */
    @Transactional
    public User findOrCreateByAppleSub(String appleSub, String email) {
        String sub = normalizeSub(appleSub);
        String normEmail = normalizeEmail(email);

        return userRepository.findByAppleSub(sub)
                .map(user -> {
                    if (user.getEmail() == null && normEmail != null) {
                        user.setEmail(normEmail);
                    }
                    if (user.getRole() == null || user.getRole().isBlank()) {
                        user.setRole("ROLE_USER");
                    } else if (!user.getRole().startsWith("ROLE_")) {
                        user.setRole("ROLE_" + user.getRole());
                    }
                    return user;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setAppleSub(sub);
                    u.setEmail(normEmail);       // null 가능
                    u.setRole("ROLE_USER");      // 기본 권한
                    return userRepository.save(u);
                });
    }

    public Optional<User> findByAppleSubOptional(String appleSub) {
        return userRepository.findByAppleSub(normalizeSub(appleSub));
    }

    public boolean existsByAppleSub(String appleSub) {
        return userRepository.existsByAppleSub(normalizeSub(appleSub));
    }

    @Transactional
    public User createUserWithApple(String appleSub, String email, String nickname) {
        String sub = normalizeSub(appleSub);
        String normEmail = normalizeEmail(email);
        if (existsByAppleSub(sub)) {
            throw new IllegalStateException("이미 가입된 사용자");
        }
        User u = new User();
        u.setAppleSub(sub);
        u.setEmail(normEmail);        // null 가능
        u.setNickname(nickname);      // 회원가입 시 필수 (엔티티에서 nullable=false라면 반드시 값 필요)
        u.setRole("ROLE_USER");
        return userRepository.save(u);
    }

    public User getByAppleSub(String appleSub) {
        return userRepository.findByAppleSub(normalizeSub(appleSub))
                .orElseThrow(() -> new UsernameNotFoundException("User not found by appleSub: " + appleSub));
    }

    public User getByEmail(String email) {
        String normEmail = normalizeEmail(email);
        return userRepository.findByEmail(normEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + normEmail));
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
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

    // ===== util =====
    private String normalizeSub(String sub) {
        return sub == null ? null : sub.trim();
    }
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
