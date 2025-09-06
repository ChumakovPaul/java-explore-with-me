package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
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
public class UpdateEventUserRequest {
    @Nullable
    @Size(min = 20, max = 2000, message = "Annotation should contain from 20 to 2000 characters")
    private String annotation;
    @Nullable
    @Positive
    private Long category;
    @Nullable
    @Size(min = 20, max = 7000, message = "Description should contain from 20 to 7000 characters")
    private String description;
    @Nullable
    @Future(message = "Event should be in future")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Nullable
    private Location location;
    private Boolean paid;
    @Nullable
    @Positive
    private Long participantLimit;
    @Nullable
    private Boolean requestModeration;
    @Nullable
    private String stateAction;
    @Nullable
    @Size(min = 3, max = 120, message = "Title should contain from 3 to 120 characters")
    private String title;

}
