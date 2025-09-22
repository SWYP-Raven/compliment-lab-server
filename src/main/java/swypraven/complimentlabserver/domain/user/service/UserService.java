package swypraven.complimentlabserver.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.dto.FindOrCreateAppleUserDto;
import swypraven.complimentlabserver.domain.user.model.request.UpdateUserRequest;
import swypraven.complimentlabserver.domain.user.model.response.UserInfoResponse;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;
import swypraven.complimentlabserver.global.auth.security.CustomUserDetails;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    public FindOrCreateAppleUserDto findOrCreate(String sub, String email) {
        return userRepository.findByEmail(email)
                .map(user -> new FindOrCreateAppleUserDto(user, true))
                .orElseGet(() -> {
                    User newUser = new User(email, sub).setRole("ROLE_USER");
                    if (newUser.getNickname() == null || newUser.getNickname().isBlank()) {
                        newUser.setNickname("사용자");
                    }
                    return new FindOrCreateAppleUserDto(userRepository.save(newUser), false);
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
        u.setEmail(normEmail);
        u.setNickname((nickname == null || nickname.isBlank()) ? "사용자" : nickname);
        u.setRole("ROLE_USER");
        return userRepository.save(u);
    }

    public User getByAppleSub(String appleSub) {
        return userRepository.findByAppleSub(normalizeSub(appleSub))
                .orElseThrow(() -> new UsernameNotFoundException("User not found by appleSub: " + appleSub));
    }

    public User getByAppleEmail(String email) {
        return userRepository.findByAppleSub(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by email: " + email));
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public UserInfoResponse updateUser(UpdateUserRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        User updatedUser = user.update(request);
        return new UserInfoResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return new UserInfoResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    public UserDetails loadUserByUsername(String appleSub) throws UsernameNotFoundException {
        User user = getByAppleSub(appleSub);
        String role = (user.getRole() == null || user.getRole().isBlank())
                ? "ROLE_USER"
                : (user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole());
        List<SimpleGrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new CustomUserDetails(user, auth);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by id: " + userId));
        String role = (user.getRole() == null || user.getRole().isBlank())
                ? "ROLE_USER"
                : (user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole());
        List<SimpleGrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new CustomUserDetails(user, auth);
    }

    private String normalizeSub(String sub) {
        return sub == null ? null : sub.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
