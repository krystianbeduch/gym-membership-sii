package pl.krystianbeduch.gymmembership.gym.dto;

public record GymResponseDto(
        Long id,
        String name,
        GymAddressResponseDto gymAddress,
        String phoneNumber
) {}