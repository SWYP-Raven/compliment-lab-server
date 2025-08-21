package swypraven.complimentlabserver.global.auth.jwt;

import lombok.Builder;

@Builder
public record JwtToken(
        String grantType,
        String accessToken,
        String refreshToken
) {}
