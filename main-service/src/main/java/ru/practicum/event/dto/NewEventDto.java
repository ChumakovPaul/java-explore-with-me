package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.event.model.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewEventDto {
    @NotBlank(message = "Annotation must not be blank")
    @Size(min = 20, max = 2000, message = "Annotation should contain from 20 to 2000 characters")
    private String annotation;
    @NotNull
    @Positive
    private Long category;
    @NotBlank(message = "Description must not be blank")
    @Size(min = 20, max = 7000, message = "Description should contain from 20 to 7000 characters")
    private String description;
    @NotNull
    @Future(message = "Event should be in future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @Positive
    private Long participantLimit;
    private Boolean requestModeration;
    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 120, message = "Title should contain from 3 to 120 characters")
    private String title;
}
