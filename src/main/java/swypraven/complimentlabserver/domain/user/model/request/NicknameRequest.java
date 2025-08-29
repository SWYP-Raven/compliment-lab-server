package swypraven.complimentlabserver.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;


public record NicknameRequest(@NotBlank String nickname, @NotBlank String identityToken) {}
