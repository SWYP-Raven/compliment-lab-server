package swypraven.complimentlabserver.domain.user.model.response;


import lombok.Getter;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
public class UserInfoResponse {

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
      
        this.friendAlarm = user.getFriendAlarm();
        this.archiveAlarm = user.getArchiveAlarm();
        this.marketingAlarm = user.getMarketingAlarm();
        this.eventAlarm = user.getEventAlarm();
    }

    private final Long id;
    private final String nickname;
    private final String email;
    private final Boolean friendAlarm;
    private final Boolean archiveAlarm;
    private final Boolean marketingAlarm;
    private final Boolean eventAlarm;
}
