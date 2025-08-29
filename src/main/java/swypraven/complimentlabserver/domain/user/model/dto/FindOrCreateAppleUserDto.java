package swypraven.complimentlabserver.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
@AllArgsConstructor
public class FindOrCreateAppleUserDto {
    private User user;
    private Boolean isSignUp;
}
