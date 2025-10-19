package ru.practicum.compilation.dto;

import jakarta.annotation.Nullable;
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
public class UpdateCompilationRequest {
    @Nullable
    private Set<Long> events;
    @Nullable
    private Boolean pinned;
    @Nullable
    @Size(min = 1, max = 50, message = "Compilation title should contain from 1 to 50 characters")
    private String title;
}
