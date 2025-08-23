package swypraven.complimentlabserver.domain.friend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestCreateFriend {

    @JsonProperty("name")
    private String name;

    @JsonProperty("friend_type")
    private String friendType;

}
