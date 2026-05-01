package pl.krystianbeduch.gymmembership.common.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ApiError of(HttpStatus status, String message) {
        return new ApiError(
                status.value(), status.getReasonPhrase(), message, LocalDateTime.now()
        );
    }
}