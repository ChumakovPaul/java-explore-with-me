package ru.practicum.compilation.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 50, message = "Compilation title should contain from 1 to 50 characters")
    private String title;
}
