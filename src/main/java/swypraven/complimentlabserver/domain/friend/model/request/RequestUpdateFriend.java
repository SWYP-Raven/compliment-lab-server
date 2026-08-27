package swypraven.complimentlabserver.domain.friend.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestUpdateFriend {

    @NotBlank(message = "친구의 이름은 필수입니다.")
    @Size(max = 20, message = "친구 이름은 20자 이하로 입력해주세요.")
    private String name;
}
