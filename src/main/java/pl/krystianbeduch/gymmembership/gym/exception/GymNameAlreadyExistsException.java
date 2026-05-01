package pl.krystianbeduch.gymmembership.gym.exception;

public class GymNameAlreadyExistsException extends RuntimeException {
    public GymNameAlreadyExistsException(String message) {
        super(message);
    }
}
