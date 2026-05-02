package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;

public final class MembershipPlanTestDataFactory {

    private MembershipPlanTestDataFactory() {}

    public static MembershipPlanCreateRequestDto createRequestDto() {
        return new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );
    }

    public static MembershipPlanResponseDto createResponseDto(Long id) {
        return new MembershipPlanResponseDto(
                id,
                1L,
                "Gym",
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );
    }
}