package pl.krystianbeduch.gymmembership.membership;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MembershipPlanCreateRequestValidationTest {

    private Validator validator;
    private static final int NAME_MAX_SIZE = 100;
    private static final int DURATION_MAX = 60;
    private static final int MAX_MEMBERS_MAX = 999;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenRequestIsValid() {
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto();
        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenNameIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                null,
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenNameIsBlank() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "   ",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenNameExceedsMaxSize() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "a".repeat(NAME_MAX_SIZE + 1),
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenTypeIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                null,
                new BigDecimal("999.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("type", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceAmountIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                null,
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceAmountIsLessThanMinimum() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("0.00"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceAmountIsNegative() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("-999.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceAmountHasTooManyFractionDigits() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("99.999"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceAmountHasTooManyIntegerDigits() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("123456789.99"),
                "PLN",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceAmount", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceCurrencyCodeIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                null,
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceCurrencyCode", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceCurrencyCodeIsBlank() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "   ",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertEquals("monthlyPriceCurrencyCode", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMonthlyPriceCurrencyCodeHasInvalidPattern() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "pln",
                6,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("monthlyPriceCurrencyCode", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenDurationInMonthsIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                null,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("durationInMonths", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenDurationInMonthsIsNotPositive() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                0,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("durationInMonths", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenDurationInMonthsExceedsMax() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                DURATION_MAX + 1,
                100
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("durationInMonths", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMaxMembersIsNull() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                null
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("maxMembers", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMaxMembersIsNotPositive() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                0
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("maxMembers", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void shouldFailValidation_whenMaxMembersExceedsMax() {
        MembershipPlanCreateRequestDto request = new MembershipPlanCreateRequestDto(
                "Premium 6M",
                MembershipPlanType.PREMIUM,
                new BigDecimal("999.99"),
                "PLN",
                6,
                MAX_MEMBERS_MAX + 1
        );

        Set<ConstraintViolation<MembershipPlanCreateRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("maxMembers", violations.iterator().next().getPropertyPath().toString());
    }
}