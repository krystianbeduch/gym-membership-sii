package pl.krystianbeduch.gymmembership.gym.dto;

import lombok.Builder;

@Builder
public record GymResponseDto(
        Long id,
        String name,
        GymAddressResponseDto gymAddress,
        String phoneNumber
) { }