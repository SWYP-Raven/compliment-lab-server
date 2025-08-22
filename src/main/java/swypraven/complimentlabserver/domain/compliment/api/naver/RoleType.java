package swypraven.complimentlabserver.domain.compliment.api.naver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
    ;
    private final String name;
}
