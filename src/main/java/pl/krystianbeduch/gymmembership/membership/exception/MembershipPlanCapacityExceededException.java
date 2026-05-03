package pl.krystianbeduch.gymmembership.membership.exception;

import pl.krystianbeduch.gymmembership.common.exception.ConflictException;

public class MembershipPlanCapacityExceededException extends ConflictException {
    public MembershipPlanCapacityExceededException(String message) {
        super(message);
    }
}