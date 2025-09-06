package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewCategoryDto {
    @NotBlank(message = "Category name must not be blank")
    @Size(min = 1, max = 50, message = "Category name should contain from 1 to 50 characters")
    private String name;
}



