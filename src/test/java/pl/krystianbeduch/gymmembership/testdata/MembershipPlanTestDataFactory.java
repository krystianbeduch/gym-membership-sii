package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;

public final class MembershipPlanTestDataFactory {

    private MembershipPlanTestDataFactory() {}

    public static MembershipPlanCreateRequestDto createRequestDto() {
        return createRequestDto("Premium 6M", 6);
    }

    public static MembershipPlanCreateRequestDto createRequestDto(String name) {
        return createRequestDto(name, 6);
    }

    public static MembershipPlanCreateRequestDto createRequestDto(int durationInMonths) {
        return createRequestDto("Premium 6M", durationInMonths);
    }

    public static MembershipPlanCreateRequestDto createRequestDto(
            String name, int durationInMonths
    ) {
        return MembershipPlanCreateRequestDto.builder()
                .name(name)
                .type(MembershipPlanType.PREMIUM)
                .monthlyPriceAmount(new BigDecimal("999.99"))
                .monthlyPriceCurrencyCode("PLN")
                .durationInMonths(durationInMonths)
                .maxMembers(100)
                .build();
    }

    public static MembershipPlanResponseDto createResponseDto(Long id) {
        return MembershipPlanResponseDto.builder()
                .id(id)
                .gymId(1L)
                .gymName("Gym")
                .name("Premium 6M")
                .type(MembershipPlanType.PREMIUM)
                .monthlyPriceAmount(new BigDecimal("999.99"))
                .monthlyPriceCurrencyCode("PLN")
                .durationInMonths(6)
                .maxMembers(100)
                .build();
    }
}