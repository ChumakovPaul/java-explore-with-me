package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GetEventsParams {
    List<Long> users;
    List<State> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    int from;
    int size;
}
