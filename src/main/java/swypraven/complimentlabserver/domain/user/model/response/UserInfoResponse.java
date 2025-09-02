package swypraven.complimentlabserver.domain.user.model.response;

import lombok.Getter;
import swypraven.complimentlabserver.domain.user.entity.User;

@Getter
public class UserInfoResponse {

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();

        // 엔티티에는 alarm 하나만 있으므로, 프론트에서 기대하는 4개 필드는 동일 값으로 매핑
        Boolean alarm = user.getAlarm();
        this.friendAlarm = alarm;
        this.archiveAlarm = alarm;
        this.marketingAlarm = alarm;
        this.eventAlarm = alarm;
    }

    private final Long id;
    private final String nickname;
    private final String email;
    private final Boolean friendAlarm;
    private final Boolean archiveAlarm;
    private final Boolean marketingAlarm;
    private final Boolean eventAlarm;
}
