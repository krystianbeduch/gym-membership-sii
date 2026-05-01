package pl.krystianbeduch.gymmembership.gym.dto;

import pl.krystianbeduch.gymmembership.gym.enums.Country;

public record GymAddressResponseDto(
        Country country,
        String city,
        String postalCode,
        String street,
        String buildingNumber,
        String apartmentNumber
) {}