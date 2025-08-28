package swypraven.complimentlabserver.domain.compliment.model.response;

import swypraven.complimentlabserver.domain.compliment.entity.TodayCompliment;

import java.time.ZoneId;

public record TodayDto(Long id, String text, String date) {
    public static TodayDto from(TodayCompliment e) {
        var kst = ZoneId.of("Asia/Seoul");
        String date = e.getCreatedAt() == null ? null : e.getCreatedAt().atZone(kst).toLocalDate().toString();
        return new TodayDto(e.getId(), e.getMessage(), date);
    }
}
