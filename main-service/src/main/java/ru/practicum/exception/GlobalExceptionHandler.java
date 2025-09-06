package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);

        String message = "Validation failed";
        if (fieldError != null) {
            String field = fieldError.getField();
            String err = fieldError.getDefaultMessage();
            Object rejected = fieldError.getRejectedValue();
            message = String.format("Field: %s. Error: %s. Value: %s", field, err, rejected);
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                message,
                LocalDateTime.now().format(FORMATTER)
        );
        return error;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFound(DataNotFoundException ex) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.name(),
                "The action is prohibited",
                ex.getMessage(),
                LocalDateTime.now().format(FORMATTER)
        );
    }

    public record ErrorResponse(String status, String reason, String message, String timestamp) {
    }
}