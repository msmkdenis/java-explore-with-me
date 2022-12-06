package ru.practicum.ewmstat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.error("EntityNotFoundException. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.NOT_FOUND);
        return new ApiError(exception, "Требуемый объект не найден.", HttpStatus.NOT_FOUND);
    }
}
