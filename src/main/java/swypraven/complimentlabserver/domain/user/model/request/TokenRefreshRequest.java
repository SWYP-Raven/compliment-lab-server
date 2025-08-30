package swypraven.complimentlabserver.domain.user.model.request;


import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 모델
 */
public record TokenRefreshRequest(
        @NotBlank String refreshToken
) {}