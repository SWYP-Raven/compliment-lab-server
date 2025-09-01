package swypraven.complimentlabserver.domain.compliment.sequence;

import java.time.LocalDate;

public interface ComplimentSequenceProvider {
    /** 해당 유저 seed와 날짜에 대응하는 compliment_id */
    int idFor(int seed, LocalDate date);

    /** 디버깅/QA용: 유저 seed의 전체 순열(compliment_id 배열) */
    int[] sequenceFor(int seed);
}
