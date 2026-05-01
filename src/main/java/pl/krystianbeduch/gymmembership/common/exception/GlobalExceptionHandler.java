package pl.krystianbeduch.gymmembership.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GymNameAlreadyExistsException.class)
    public ApiError handleGymNameAlreadyException(GymNameAlreadyExistsException e) {
        return ApiError.of(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
         String message = ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .findFirst()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .orElse("Validation failed");

         return ApiError.of(HttpStatus.BAD_REQUEST, message);
    }
}