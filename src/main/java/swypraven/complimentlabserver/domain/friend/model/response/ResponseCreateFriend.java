package swypraven.complimentlabserver.domain.friend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

@Getter
public class ResponseCreateFriend {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type_name")
    private String typeName;

    @JsonProperty("type_id")
    private Long typeId;

    public ResponseCreateFriend(Friend friend) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeName =  friend.getType().getName();
        this.typeId = friend.getType().getId();
    }

}
