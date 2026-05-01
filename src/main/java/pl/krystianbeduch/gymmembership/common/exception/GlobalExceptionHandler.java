package pl.krystianbeduch.gymmembership.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;
import pl.krystianbeduch.gymmembership.gym.exception.GymNotFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GymNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleGymNameAlreadyException(GymNameAlreadyExistsException e) {
        return ApiError.of(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(GymNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleGymNotFoundException(GymNotFoundException e) {
        return ApiError.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
         String message = ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .findFirst()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .orElse("Validation failed");

         return ApiError.of(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType() != null
                && invalidFormatException.getTargetType().isEnum()) {

            String fieldName = invalidFormatException.getPath()
                    .stream()
                    .map(JacksonException.Reference::getPropertyName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            String allowedValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String message = "Invalid value for field '%s'. Allowed values: %s"
                    .formatted(fieldName, allowedValues);

            return ApiError.of(HttpStatus.BAD_REQUEST, message);
        }

        return ApiError.of(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }
}