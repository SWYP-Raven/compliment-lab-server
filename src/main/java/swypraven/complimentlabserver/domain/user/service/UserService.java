package swypraven.complimentlabserver.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    //사용자 관리
    private final UserRepository userRepository;
    /**
     * Apple sub(고유 ID) 기준으로 조회하고 없으면 생성
     * email은 첫 로그인에만 제공될 수 있으니 null 허용, 업데이트 가능하게 처리
     */
    @Transactional
    public User findOrCreateByAppleSub(String appleSub, String email) {
        return userRepository.findByAppleSub(appleSub)
                .map(user -> {
                    // 기존 유저인데 최초 이후에 email이 새로 들어왔으면 업데이트(선택)
                    if (user.getEmail() == null && email != null) {
                        user.setEmail(email);
                    }
                    return user;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setAppleSub(appleSub);
                    u.setEmail(email);         // null 가능
                    u.setRole("ROLE_USER");          // 기본 권한
                    return userRepository.save(u);
                });
    }

    @Transactional
    public User findOrCreateByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalStateException("존재하지 않은 유저: "+email));
    }

    @Transactional
    public Optional<User> findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

}
