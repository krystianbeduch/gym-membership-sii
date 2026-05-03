package pl.krystianbeduch.gymmembership.member.exception;

import pl.krystianbeduch.gymmembership.common.exception.ConflictException;

public class MemberEmailAlreadyExistsException extends ConflictException {
    public MemberEmailAlreadyExistsException(String message) {
        super(message);
    }
}