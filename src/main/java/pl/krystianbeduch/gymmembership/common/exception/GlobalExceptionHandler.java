package pl.krystianbeduch.gymmembership.common.exception;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler responsible for translating application and validation
 * exceptions into consistent API error responses.
 *
 * <p>This class centralizes error handling for all REST controllers, so controllers
 * and services do not need to duplicate response-building logic.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the business case when a gym with the same name already exists.
     *
     * <p>Used for situations where the request is valid but cannot be completed due to
     * the current state of the system, for instance, when the provided gym name already
     * exist or when the maximum membership plan capacity.
     * Maps the error to HTTP 409 Conflict.</p>
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleGymNameAlreadyException(ConflictException e) {
        return ApiError.of(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Handles the case when the requested resource cannot be found.
     *
     * <p>Used when an entity identified by an ID or another lookup key does not exist,
     * for instance, a gym, membership plan or member.
     * Maps the error to HTTP 404 Not Found.</p>
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleGymNotFoundException(NotFoundException e) {
        return ApiError.of(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Handles validation errors for request bodies validated with @Valid.
     *
     * <p>This exception is typically thrown when a JSON request body is bound to a DTO
     * and bean validation fails, for example, when a field is blank, null, too long,
     * or has an invalid format.
     * Returns the first validation message as HTTP 400 Bad Request.</p>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(
            MethodArgumentNotValidException ex
    ) {
         String message = ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .findFirst()
                 .map(DefaultMessageSourceResolvable::getDefaultMessage)
                 .orElse("Validation failed");

         return ApiError.of(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles validation errors for controller method parameters.
     *
     * <p>This exception is typically thrown when validation fails for method parameters
     * such as @PathVariable or other directly validated handler method
     * arguments. Returns the first validation message as HTTP 400 Bad Request.</p>
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHandlerMethodValidationException(
            HandlerMethodValidationException ex
    ) {
        String message = ex.getAllErrors()
                .stream()
                .findFirst()
                .map(MessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        return ApiError.of(HttpStatus.BAD_REQUEST, message);
    }

    /**
    * Handles invalid enum values in HTTP request bodies.
    *
    * <p>This exception is thrown when the incoming JSON contains a value that cannot
    * be mapped to the target enum type. In that case, the response includes the field
    * name and the list of allowed enum values. For all other parsing problems,
    * a generic malformed JSON message is returned.</p>
    */
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