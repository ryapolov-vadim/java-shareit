package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handAccessDenied(final AccessDeniedException e) {
        return new ErrorResponse("Ошибка, нет прав", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handNotFound(final NotFoundException e) {
        return new ErrorResponse("Ошибка, искомый объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handValidation(final ValidationException e) {
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse headConflict(final ConflictException e) {
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }
}
