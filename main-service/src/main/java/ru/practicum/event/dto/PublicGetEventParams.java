package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PublicGetEventParams {
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    boolean onlyAvailable;
    Sort sort;
    int from;
    int size;
}
