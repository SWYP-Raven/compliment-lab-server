package swypraven.complimentlabserver.domain.friend.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import swypraven.complimentlabserver.domain.friend.entity.Friend;
import swypraven.complimentlabserver.domain.friend.model.dto.LastMessageDto;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFriend {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;


    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("last_message")
    private LastMessageDto lastMessage;

    @JsonProperty("is_first")
    private Boolean isFirst;

    public ResponseFriend(Friend friend) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeId = friend.getType().getId().toString();
    }

    public ResponseFriend(Friend friend, Boolean isFirst, LastMessageDto lastMessageDto) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeId = friend.getType().getId().toString();
        this.isFirst = isFirst;
        this.lastMessage = lastMessageDto;
    }

    public ResponseFriend(Friend friend, String lastMessage, LocalDateTime time) {
        this.id = friend.getId();
        this.name = friend.getName();
        this.typeId = friend.getType().getId().toString();
        this.lastMessage = new  LastMessageDto(lastMessage, time);
    }
}
