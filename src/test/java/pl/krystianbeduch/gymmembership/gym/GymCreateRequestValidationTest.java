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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(null)
                .gymAddress(validAddress())
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name("  ")
                .gymAddress(validAddress())
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name("a".repeat(NAME_MAX_SIZE + 1))
                .gymAddress(validAddress())
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(null)
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(validAddress())
                .phoneNumber("  ")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(validAddress())
                .phoneNumber("1".repeat(PHONE_MAX_SIZE + 1))
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(GymAddressRequestDto.builder()
                        .country(Country.POLAND)
                        .city("  ")
                        .postalCode("11-111")
                        .street("Street")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(GymAddressRequestDto.builder()
                        .country(null)
                        .city("City")
                        .postalCode("11-111")
                        .street("Street")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();

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
        GymCreateRequestDto request = GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(GymAddressRequestDto.builder()
                        .country(Country.POLAND)
                        .city("City")
                        .postalCode("1".repeat(POSTAL_CODE_MAX_SIZE + 1))
                        .street("Street")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();

        Set<ConstraintViolation<GymCreateRequestDto>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "gymAddress.postalCode",
                violations.iterator().next().getPropertyPath().toString()
        );
    }

    private GymCreateRequestDto validRequest() {
        return GymCreateRequestDto.builder()
                .name(uniqueName)
                .gymAddress(validAddress())
                .phoneNumber("123")
                .build();
    }

    private GymAddressRequestDto validAddress() {
        return GymAddressRequestDto.builder()
                .country(Country.POLAND)
                .city("City")
                .postalCode("11-111")
                .street("Street")
                .buildingNumber("1")
                .build();
    }
}