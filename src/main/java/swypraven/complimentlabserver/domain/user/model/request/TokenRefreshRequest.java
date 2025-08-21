package swypraven.complimentlabserver.domain.user.model.request;


/**
 * 토큰 갱신 요청 모델
 */
public record TokenRefreshRequest(
        @jakarta.validation.constraints.NotBlank String refreshToken
) {}