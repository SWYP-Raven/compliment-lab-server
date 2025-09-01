package swypraven.complimentlabserver.domain.user.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import swypraven.complimentlabserver.domain.user.entity.User;
import swypraven.complimentlabserver.domain.user.model.dto.FindOrCreateAppleUserDto;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class AppleAuthResponse {

    // 로그인
    public AppleAuthResponse(JwtToken token, FindOrCreateAppleUserDto findOrCreateResponse) {
        this.userId = findOrCreateResponse.getUser().getId();
        this.isSignup = findOrCreateResponse.getIsSignUp();
        this.accessToken = token.accessToken();
        this.refreshToken = token.refreshToken();
    }

    // 회원 가입
    public AppleAuthResponse(FindOrCreateAppleUserDto findOrCreateResponse) {
        this.userId = findOrCreateResponse.getUser().getId();
        this.isSignup = findOrCreateResponse.getIsSignUp();
    }

    public AppleAuthResponse(User user, JwtToken token) {
        this.userId = user.getId();
        this.accessToken = token.accessToken();
        this.refreshToken = token.refreshToken();
    }

    private Long userId;
    private  Boolean isSignup;
    private  String accessToken;
    private  String refreshToken;
}
