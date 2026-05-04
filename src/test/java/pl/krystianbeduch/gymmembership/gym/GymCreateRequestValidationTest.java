package pl.krystianbeduch.gymmembership.gym;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.enums.Country;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GymCreateRequestValidationTest {

    private String uniqueName;
    private Validator validator;
    private static final int NAME_MAX_SIZE = 100;
    private static final int PHONE_MAX_SIZE = 20;
    private static final int POSTAL_CODE_MAX_SIZE = 15;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        uniqueName = UUID.randomUUID().toString();
    }

    @Test
    void shouldPassValidation_whenRequestIsValid() {
        GymCreateRequestDto requestDto = validRequest();
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(requestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenNameIsNull() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                null,
                validAddress(),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "name",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenNameIsBlank() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                "   ",
                validAddress(),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "name",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenNameExceedsMaxSize() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                "a".repeat(NAME_MAX_SIZE + 1),
                validAddress(),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "name",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenGymAddressIsNull() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                null,
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "gymAddress",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenPhoneNumberIsBlank() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                validAddress(),
                "   "
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "phoneNumber",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenPhoneNumberExceedsMaxSize() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                validAddress(),
                "1".repeat(PHONE_MAX_SIZE + 1)
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "phoneNumber",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenCityIsBlank() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                new GymAddressRequestDto(
                        Country.POLAND,
                        "   ",
                        "11-111",
                        "Street",
                        "1",
                        null
                ),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "gymAddress.city",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

     @Test
    void shouldFailValidation_whenCountryIsNull() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                new GymAddressRequestDto(
                        null,
                        "City",
                        "11-111",
                        "Street",
                        "1",
                        null
                ),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "gymAddress.country",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    @Test
    void shouldFailValidation_whenPostalCodeExceedsMaxSize() {
        GymCreateRequestDto request = new GymCreateRequestDto(
                uniqueName,
                new GymAddressRequestDto(
                        Country.POLAND,
                        "City",
                        "1".repeat(POSTAL_CODE_MAX_SIZE + 1),
                        "Street",
                        "1",
                        null
                ),
                "123"
        );
        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "gymAddress.postalCode",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    private GymCreateRequestDto validRequest() {
        return new GymCreateRequestDto(
                uniqueName,
                validAddress(),
                "123"
        );
    }

    private GymAddressRequestDto validAddress() {
        return new GymAddressRequestDto(
                Country.POLAND,
                "City",
                "11-111",
                "Street",
                "1",
                null
        );
    }
}