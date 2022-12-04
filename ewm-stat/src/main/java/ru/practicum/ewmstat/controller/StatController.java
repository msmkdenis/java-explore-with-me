package ru.practicum.ewmstat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmstat.dto.NewEndPointHitDto;
import ru.practicum.ewmstat.dto.ViewStatsDto;
import ru.practicum.ewmstat.service.StatService;
import ru.practicum.ewmstat.specification.StatRequestParameters;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public void saveHit(@RequestBody NewEndPointHitDto hit) {
        log.info("Получен объект EndPointHit hit: {} ", hit);
        statService.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(StatRequestParameters parameters) {
        log.info("Получен запрос: {} ", parameters);
        return statService.getStats(parameters);
    }
}
