package ru.practicum.ewmmain.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewmmain.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError {

    private final String message;
    private final String reason;
    private final HttpStatus status;
    @JsonFormat(pattern = Constants.datePattern)
    private final LocalDateTime timestamp;
    private final List<StackTraceElement> errors;

    public ApiError(Throwable exception, String reason, HttpStatus status) {
        this.message = exception.getMessage();
        this.reason = reason;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = List.of(exception.getStackTrace());
    }
}
