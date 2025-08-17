package swypraven.complimentlabserver.domain.user.model.response;


import lombok.Builder;

@Builder
public record AppleLoginResponse(
        String grantType,
        String accessToken,
        String refreshToken
) {}