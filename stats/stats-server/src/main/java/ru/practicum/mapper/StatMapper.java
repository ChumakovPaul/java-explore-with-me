package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.StatCreateDto;
import ru.practicum.StatDto;
import ru.practicum.model.Stat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Component
public class StatMapper {
    public Stat toStat(StatCreateDto statCreateDto) {
        Stat stat = new Stat();
        stat.setApp(statCreateDto.getApp());
        stat.setUri(statCreateDto.getUri());
        stat.setIp(statCreateDto.getIp());
        stat.setTimestamp(statCreateDto.getTimestamp());
        return stat;
    }

    public StatDto toStatDto(Stat stat) {
        StatDto statDto = new StatDto();
        statDto.setApp(stat.getApp());
        statDto.setUri(stat.getUri());
        return statDto;
    }
}





