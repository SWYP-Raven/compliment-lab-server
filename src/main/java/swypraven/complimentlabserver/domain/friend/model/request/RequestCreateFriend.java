package swypraven.complimentlabserver.domain.friend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestCreateFriend {

    @NotBlank(message = "친구의 이름은 필수 입니다.")
    @Size(max = 20, message = "친구 이름은 20자 이하로 입력해주세요.")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "친구 타입은 필수입니다.")
    @JsonProperty("friend_type")
    private String friendType;

}
