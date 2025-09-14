package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AdminGetEventsParams {
    List<Long> users;
    List<State> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    int from;
    int size;
}
