package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatCreateDto;
import ru.practicum.StatDto;
import ru.practicum.mapper.StatMapper;
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
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The start date must be earlier than the end date.");
        }
        return unique ? statsRepository.findStatsUnique(start, end, uris) : statsRepository.findStats(start, end, uris);
    }
}
