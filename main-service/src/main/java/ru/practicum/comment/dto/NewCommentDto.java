package ru.practicum.comment.dto;

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
public class NewCommentDto {
    @NotBlank(message = "Comment must not be blank")
    @Size(min = 1, max = 2000, message = "Comment should contain from 1 to 2000 characters")
    private String text;
}
