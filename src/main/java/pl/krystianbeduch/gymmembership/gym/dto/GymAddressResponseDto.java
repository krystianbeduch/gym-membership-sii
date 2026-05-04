package pl.krystianbeduch.gymmembership.gym.dto;

import lombok.Builder;
import pl.krystianbeduch.gymmembership.gym.enums.Country;

@Builder
public record GymAddressResponseDto(
        Country country,
        String city,
        String postalCode,
        String street,
        String buildingNumber,
        String apartmentNumber
) { }