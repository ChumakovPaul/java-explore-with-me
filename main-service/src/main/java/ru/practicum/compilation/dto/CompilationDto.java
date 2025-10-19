package ru.practicum.compilation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private boolean pinned;
    private String title;
}
