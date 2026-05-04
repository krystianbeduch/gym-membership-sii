package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;
import java.util.Currency;

public final class MembershipPlanTestDataFactory {

    private MembershipPlanTestDataFactory() {}

    public static MembershipPlan createMembershipPlan(
            int maxMembers, Gym gym
    ) {
        return MembershipPlan.builder()
                .name("Premium 6M")
                .type(MembershipPlanType.PREMIUM)
                .monthlyPrice(new Money(
                        new BigDecimal("99.99"),
                        Currency.getInstance("PLN"))
                )
                .durationInMonths(1)
                .maxMembers(maxMembers)
                .gym(gym)
                .build();
    }

    public static MembershipPlanCreateRequestDto createRequestDto() {
        return createRequestDto("Premium 6M");
    }

    public static MembershipPlanCreateRequestDto createRequestDto(String name) {
        return new MembershipPlanCreateRequestDto(
                name,
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