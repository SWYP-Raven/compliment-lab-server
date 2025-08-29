package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentService;
//오늘의 칭찬 제공
@RestController
@RequiredArgsConstructor
@RequestMapping("/compliments")
//GET /api/v1/compliments/today
//오늘 날짜에 해당하는 칭찬 문장을 DB(today_compliment)에서 찾아 오늘의 칭찬 원본을 리턴
// 읽기 전공(오늘의 칭찬 한 문장
//DTO : todayDTo(그날의 문장, 타입)
public class ComplimentController {

    private final ComplimentService complimentService;

    @GetMapping("/today")
    public ResponseEntity<TodayDto> getToday() {
        return ResponseEntity.ok(complimentService.getTodayOrThrow());
    }
}
