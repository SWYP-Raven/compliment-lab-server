package swypraven.complimentlabserver.global.auth.jwt;

import java.io.Serializable;

public record CustomUserPrincipal(Long id, String subject, String role) implements Serializable {}
