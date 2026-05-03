package pl.krystianbeduch.gymmembership.gym.exception;

import pl.krystianbeduch.gymmembership.common.exception.ConflictException;

public class GymNameAlreadyExistsException extends ConflictException {
    public GymNameAlreadyExistsException(String message) {
        super(message);
    }
}