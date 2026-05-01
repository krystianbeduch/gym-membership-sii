package pl.krystianbeduch.gymmembership.gym.exception;

public class GymNotFoundException extends RuntimeException {
    public GymNotFoundException(String message) {
        super(message);
    }
}