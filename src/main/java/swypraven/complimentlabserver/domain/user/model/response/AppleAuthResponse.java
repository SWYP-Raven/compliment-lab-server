package swypraven.complimentlabserver.domain.user.model.response;


import lombok.Builder;

@Builder
public record AppleAuthResponse(
        Boolean isSignup,
        String grantType,
        String accessToken,
        String refreshToken,
        String email,
        String nickname,
        String role
) {}