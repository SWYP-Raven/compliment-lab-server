package swypraven.complimentlabserver.domain.user.model.request;

public record UpdateUserRequest(
        String nickname,
        Boolean friendAlarm,
        Boolean archiveAlarm,
        Boolean marketingAlarm,
        Boolean eventAlarm
) {
    /** nickname이 null이거나 공백이면 기본값 "사용자" 반환 */
    public String safeNickname() {
        return (nickname == null || nickname.isBlank()) ? "사용자" : nickname;
    }

    public boolean safeFriendAlarm() {
        return friendAlarm != null && friendAlarm;
    }

    public boolean safeArchiveAlarm() {
        return archiveAlarm != null && archiveAlarm;
    }

    public boolean safeMarketingAlarm() {
        return marketingAlarm != null && marketingAlarm;
    }

    public boolean safeEventAlarm() {
        return eventAlarm != null && eventAlarm;
    }
}
