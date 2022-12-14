package ru.practicum.ewmmain.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exception) {
        log.error("EntityNotFoundException. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.NOT_FOUND);
        return new ApiError(exception, "Требуемый объект не найден.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictError(final ConstraintViolationException exception) {
        log.error("ConstraintViolationException. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.CONFLICT);
        return new ApiError(exception, "Запрос приводит к нарушению целостности данных.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.error("IllegalArgumentException. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ApiError(exception, "Ошибка запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenError(final ForbiddenError exception) {
        log.error("ForbiddenError. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.FORBIDDEN);
        return new ApiError(exception, "Не выполнены условия для совершения операции.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        log.error("ConflictError. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.CONFLICT);
        return new ApiError(exception, "Запрос приводит к нарушению целостности данных.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictError(final ConflictError exception) {
        log.error("ConflictError. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.CONFLICT);
        return new ApiError(exception, "Запрос приводит к нарушению целостности данных.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable exception) {
        log.error("ThrowableError. Произошла ошибка {}, статус ошибки {}", exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ApiError(exception, "Произошла ошибка.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleThrowable(final MissingServletRequestParameterException exception) {
        log.error("MissingServletRequestParameterException. " +
                        "Произошла ошибка {}, статус ошибки {}", exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ApiError(exception, "Произошла ошибка.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException. " +
                "Произошла ошибка {}, статус ошибки {}", exception.getMessage(), HttpStatus.BAD_REQUEST);
        return new ApiError(exception, "Произошла ошибка.", HttpStatus.BAD_REQUEST);
    }
}
