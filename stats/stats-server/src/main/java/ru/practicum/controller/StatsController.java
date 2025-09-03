package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatCreateDto;
import ru.practicum.StatDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/hit")
    public void createStat(@RequestBody @Valid StatCreateDto statCreateDto) {
        log.info("Start creating event hit: {}", statCreateDto);
        statsService.save(statCreateDto);
        log.info("Finish creating event hit: {}", statCreateDto);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/stats")
    public Collection<StatDto> getStats(
            @RequestParam(required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        log.info("Start getting event hits from {} to {} on {}", start, end, uris);
        return statsService.getStats(start, end, uris, unique);
    }
}
