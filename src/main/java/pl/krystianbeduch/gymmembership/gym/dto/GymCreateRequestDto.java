package pl.krystianbeduch.gymmembership.gym.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GymCreateRequestDto(
        @NotBlank(message = "{gym.name.notBlank}")
        @Size(max = 100, message = "{gym.name.size}")
        String name,

        @NotNull(message = "{gym.address.notNull}")
        @Valid
        GymAddressRequestDto gymAddress,

        @NotBlank(message = "{gym.phoneNumber.notBlank}")
        @Size(max = 20, message = "{gym.phoneNumber.size}")
        String phoneNumber
) { }