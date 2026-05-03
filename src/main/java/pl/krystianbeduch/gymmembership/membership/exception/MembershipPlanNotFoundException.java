package pl.krystianbeduch.gymmembership.membership.exception;

import pl.krystianbeduch.gymmembership.common.exception.NotFoundException;

public class MembershipPlanNotFoundException extends NotFoundException {
    public MembershipPlanNotFoundException(String message) {
        super(message);
    }
}