package swypraven.complimentlabserver.domain.compliment.model.api.naver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NaverApiRoleType implements RoleType{
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
    ;
    private final String message;
}
