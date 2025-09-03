package swypraven.complimentlabserver.domain.friend.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import swypraven.complimentlabserver.domain.friend.entity.Friend;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFriend {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type_name")
    private String typeName;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("last_message")
    private String lastMessage;

    @JsonProperty("is_first")
    private Boolean isFirst;

    public ResponseFriend(Friend friend) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeName =  friend.getType().getName();
        this.typeId = friend.getType().getId();
    }

    public ResponseFriend(Friend friend, Boolean isFirst) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeName =  friend.getType().getName();
        this.typeId = friend.getType().getId();
        this.isFirst = isFirst;
    }

    public ResponseFriend(Friend friend, String lastMessage) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeName =  friend.getType().getName();
        this.typeId = friend.getType().getId();
        this.lastMessage = lastMessage;
    }
}
