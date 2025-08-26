// 회원가입 요청
package swypraven.complimentlabserver.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;

public record AppleSignupRequest(
        @NotBlank String identityToken,
        @NotBlank String nickname
) {}
