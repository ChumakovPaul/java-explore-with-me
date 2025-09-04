package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
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
public class NewUserRequest {
    @NotBlank(message = "User email must not be blank")
    @Email(message = "User email must have email form")
    @Size(min = 6, max = 254,message = "User email should contain from 6 to 254 characters")
    private String email;
    @NotBlank(message = "User name must not be blank")
    @Size(min = 2, max = 250, message = "User name should contain from 2 to 250 characters")
    private String name;
}