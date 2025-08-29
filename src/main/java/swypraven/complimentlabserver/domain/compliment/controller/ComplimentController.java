package swypraven.complimentlabserver.domain.compliment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swypraven.complimentlabserver.domain.compliment.model.response.TodayDto;
import swypraven.complimentlabserver.domain.compliment.service.ComplimentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compliments")
public class ComplimentController {

    private final ComplimentService complimentService;

    @GetMapping("/today")
    public ResponseEntity<TodayDto> getToday() {
        return ResponseEntity.ok(complimentService.getTodayOrThrow());
    }
}
