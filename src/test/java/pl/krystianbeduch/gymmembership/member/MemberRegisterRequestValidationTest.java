package pl.krystianbeduch.gymmembership.member;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.testdata.MemberTestDataFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberRegisterRequestValidationTest {

    private Validator validator;
    private static final int NAME_MAX_SIZE = 64;
    private static final int EMAIL_MAX_SIZE = 100;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenRequestIsValid() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenFirstNameIsBlank() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto("  ");
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "firstName",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenFirstNameExceedsMaxSize() {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto(
                    "a".repeat(NAME_MAX_SIZE + 1)
                );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "firstName",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenLastNameIsBlank() {
        MemberRegisterToMembershipRequestDto request = new MemberRegisterToMembershipRequestDto(
                "John",
                "  ",
                "john.doe@example.com"
        );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "lastName",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenLastNameExceedsMaxSize() {
        MemberRegisterToMembershipRequestDto request = new MemberRegisterToMembershipRequestDto(
                "John",
                "a".repeat(NAME_MAX_SIZE + 1),
                "john.doe@example.com"
        );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "lastName",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenEmailIsBlank() {
        MemberRegisterToMembershipRequestDto request = new MemberRegisterToMembershipRequestDto(
                "John",
                "Doe",
                "  "
        );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(
                "email",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenEmailIsInvalid() {
        MemberRegisterToMembershipRequestDto request = new MemberRegisterToMembershipRequestDto(
                "John",
                "Doe",
                "invalid-email"
        );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "email",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenEmailExceedsMaxSize() {
        MemberRegisterToMembershipRequestDto request = new MemberRegisterToMembershipRequestDto(
                "John",
                "Doe",
                "a".repeat(EMAIL_MAX_SIZE) + "@example.com"
        );
        Set<ConstraintViolation<MemberRegisterToMembershipRequestDto>> violations =
                validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(
                "email",
                violations.iterator().next().getPropertyPath().toString()
        );
    }
}