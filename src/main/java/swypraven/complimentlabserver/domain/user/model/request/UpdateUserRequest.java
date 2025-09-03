package swypraven.complimentlabserver.domain.user.model.request;

public record UpdateUserRequest(String nickname,
                                Boolean friendAlarm,
                                Boolean archiveAlarm,
                                Boolean marketingAlarm,
                                Boolean eventAlarm
){}
