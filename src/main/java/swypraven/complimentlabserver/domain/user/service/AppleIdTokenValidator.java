package swypraven.complimentlabserver.domain.user.service;

import com.nimbusds.jwt.JWTClaimsSet;


public interface AppleIdTokenValidator {
    JWTClaimsSet validate(String idToken);

}
