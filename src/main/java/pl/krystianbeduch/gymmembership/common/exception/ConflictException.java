package pl.krystianbeduch.gymmembership.common.exception;

public abstract class ConflictException extends RuntimeException {
    protected ConflictException(String message) {
        super(message);
    }
}