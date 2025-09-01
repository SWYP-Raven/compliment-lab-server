package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.dto.FindOrCreateAppleUserDto;
import swypraven.complimentlabserver.domain.user.model.response.AppleAuthResponse;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;
import swypraven.complimentlabserver.global.auth.jwt.JwtTokenProvider;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleIdTokenValidator appleIdTokenValidator;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인: 존재하는 사용자만 허용
    public AppleAuthResponse appleLogin(String idToken) throws ParseException {
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String email = claims.getStringClaim("email"); // null 가능

        User user = userService.getByAppleEmail(email);
        JwtToken token = issue(user);

        return new AppleAuthResponse(user, token);
    }


    public AppleAuthResponse appleSignUp(String idToken, String nickname) throws ParseException {
        JWTClaimsSet claims = appleIdTokenValidator.validate(idToken);
        String sub = claims.getSubject();
        String email = claims.getStringClaim("email"); // null 가능

        FindOrCreateAppleUserDto appleUser = userService.findOrCreate(sub, email, nickname);

        JwtToken token = issue(appleUser.getUser());

        return new AppleAuthResponse(appleUser.getUser(), token);
    }


    private JwtToken issue(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getAppleSub(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
        return jwtTokenProvider.generateToken(authentication, user);
    }
}
