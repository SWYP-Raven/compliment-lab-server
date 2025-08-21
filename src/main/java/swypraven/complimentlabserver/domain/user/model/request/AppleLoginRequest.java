package swypraven.complimentlabserver.domain.user.model.request;
public record AppleLoginRequest(
        @jakarta.validation.constraints.NotBlank String identityToken
) {}