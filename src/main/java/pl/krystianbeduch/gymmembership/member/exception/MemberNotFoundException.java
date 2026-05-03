package pl.krystianbeduch.gymmembership.member.exception;

import pl.krystianbeduch.gymmembership.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}