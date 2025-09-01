package ru.practicum;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsClient {
    void hit(StatCreateDto statCreateDto);

    Collection<StatDto> getStat(String start,
                                String end,
                              List<String> uris,
                              Boolean unique);
}
