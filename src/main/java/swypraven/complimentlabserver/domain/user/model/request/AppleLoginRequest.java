package swypraven.complimentlabserver.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest(
        @NotBlank String identityToken
) {}
