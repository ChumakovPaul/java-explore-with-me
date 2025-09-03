package ru.practicum.service;

import ru.practicum.StatCreateDto;
import ru.practicum.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void save(StatCreateDto statCreateDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
