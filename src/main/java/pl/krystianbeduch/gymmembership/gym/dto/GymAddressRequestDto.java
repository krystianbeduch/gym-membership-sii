package pl.krystianbeduch.gymmembership.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.krystianbeduch.gymmembership.gym.enums.Country;

public record GymAddressRequestDto(

        @NotNull(message = "{gym.address.country.notNull}")
        Country country,

        @NotBlank(message = "{gym.address.city.notBlank}")
        @Size(max = 60, message = "{gym.address.city.size}")
        String city,

        @NotBlank(message = "{gym.address.postalCode.notBlank}")
        @Size(max = 15, message = "{gym.address.postalCode.size}")
        String postalCode,

        @NotBlank(message = "{gym.address.street.notBlank}")
        @Size(max = 60, message = "{gym.address.street.size}")
        String street,

        @NotBlank(message = "{gym.address.buildingNumber.notBlank}")
        @Size(max = 15, message = "{gym.address.buildingNumber.size}")
        String buildingNumber,

        @Size(max = 15, message = "{gym.address.apartmentNumber.size}")
        String apartmentNumber
) {}