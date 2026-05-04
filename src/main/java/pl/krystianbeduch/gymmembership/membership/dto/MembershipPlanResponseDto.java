package pl.krystianbeduch.gymmembership.membership.dto;

import lombok.Builder;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;

@Builder
public record MembershipPlanResponseDto(
        Long id,
        Long gymId,
        String gymName,
        String name,
        MembershipPlanType type,
        BigDecimal monthlyPriceAmount,
        String monthlyPriceCurrencyCode,
        Integer durationInMonths,
        Integer maxMembers
) {}