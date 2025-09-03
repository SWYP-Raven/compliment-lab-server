package swypraven.complimentlabserver.domain.user.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import swypraven.complimentlabserver.domain.user.model.dto.FindOrCreateAppleUserDto;
import swypraven.complimentlabserver.global.auth.jwt.JwtToken;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class AppleAuthResponse {


    public AppleAuthResponse(FindOrCreateAppleUserDto dto, JwtToken token) {
        this.userId = dto.getUser().getId();
        this.accessToken = token.accessToken();
        this.refreshToken = token.refreshToken();
        this.isSignup = dto.getIsSignUp();

    }

    private Long userId;
    private  Boolean isSignup;
    private  String accessToken;
    private  String refreshToken;
}
