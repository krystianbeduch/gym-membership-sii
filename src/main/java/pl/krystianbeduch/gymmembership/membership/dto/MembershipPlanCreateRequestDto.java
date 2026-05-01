package pl.krystianbeduch.gymmembership.membership.dto;

import jakarta.validation.constraints.*;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;

public record MembershipPlanCreateRequestDto (

        @NotBlank(message = "{membershipPlan.name.notBlank}")
        @Size(max = 100, message = "{membershipPlan.name.size}")
        @Size(max = 100)
        String name,

        @NotNull(message = "{membershipPlan.type.notNull}")
        MembershipPlanType type,

        @NotNull(message = "{membershipPlan.monthlyPriceAmount.notNull}")
        @DecimalMin(value = "0.01", message = "{membershipPlan.monthlyPriceAmount.decimalMin}")
        @Digits(integer = 8, fraction = 2, message = "{membershipPlan.monthlyPriceAmount.digits}")
        BigDecimal monthlyPriceAmount,

        @NotBlank(message = "{membershipPlan.monthlyPriceCurrencyCode.notBlank}")
        @Pattern(regexp = "^[A-Z]{3}$", message = "{membershipPlan.monthlyPriceCurrencyCode.pattern}")
        String monthlyPriceCurrencyCode,

        @NotNull(message = "{membershipPlan.durationInMonths.notNull}")
        @Positive(message = "{membershipPlan.durationInMonths.positive}")
        @Max(value = 60, message = "{membershipPlan.durationInMonths.max}")
        Integer durationInMonths,

        @NotNull(message = "{membershipPlan.maxMembers.notNull}")
        @Positive(message = "{membershipPlan.maxMembers.positive}")
        @Max(value = 999, message = "{membershipPlan.maxMembers.max}")
        Integer maxMembers
){ }