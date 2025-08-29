package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentService;
import swypraven.complimentlabserver.global.auth.jwt.CustomUserPrincipal;

//오늘의 칭찬 제공
@RestController
@RequiredArgsConstructor
@RequestMapping("/compliments")
//GET compliments/today
//오늘 날짜에 해당하는 칭찬 문장을 DB(today_compliment)에서 찾아 오늘의 칭찬 원본을 리턴
// 읽기 전공(오늘의 칭찬 한 문장
// 중복 처리
//DTO : todayDTo(그날의 문장, 타입)
public class ComplimentController {
    private final ComplimentService complimentService;

    /** KST 기준 오늘의 칭찬 */
    @GetMapping("/today")
    public ResponseEntity<TodayDto> getToday() {
        return ResponseEntity.ok(complimentService.getTodayOrThrow());
    }

    /** 랜덤 칭찬(로그인 필요) */
    @PostMapping("/random")
    public ResponseEntity<TodayDto> getRandom(@AuthenticationPrincipal CustomUserPrincipal me) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        TodayDto dto = complimentService.pickRandomForUser(me.id());
        return ResponseEntity.ok(dto);
    }

    /** 랜덤 칭찬 읽음 처리 전체(로그인 필요) */
    @PostMapping("/random/read-all")
    public ResponseEntity<Void> markRandomReadAll(@AuthenticationPrincipal CustomUserPrincipal me) {
        if (me == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        complimentService.markAllRandomLogsRead(me.id());
        return ResponseEntity.noContent().build();
    }
}
