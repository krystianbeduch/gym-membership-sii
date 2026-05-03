package pl.krystianbeduch.gymmembership.gym.exception;

import pl.krystianbeduch.gymmembership.common.exception.NotFoundException;

public class GymNotFoundException extends NotFoundException {
    public GymNotFoundException(String message) {
        super(message);
    }
}