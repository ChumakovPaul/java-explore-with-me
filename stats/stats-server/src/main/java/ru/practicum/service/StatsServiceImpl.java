package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatDto;
import ru.practicum.mapper.StatMapper;
import ru.practicum.StatCreateDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatMapper statMapper;


    public void save(StatCreateDto statCreateDto) {
        statsRepository.save(statMapper.toStat(statCreateDto));
    }


public List<StatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
    if (unique) {
        return statsRepository.findStatsUnique(start, end, uris == null || uris.isEmpty() ? null : uris);
    }
    return statsRepository.findStats(start, end, uris == null || uris.isEmpty() ? null : uris);
}
    }
