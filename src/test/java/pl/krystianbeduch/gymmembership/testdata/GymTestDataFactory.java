package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;
import pl.krystianbeduch.gymmembership.gym.enums.Country;

import java.util.UUID;

public final class GymTestDataFactory {

    private GymTestDataFactory() {}

    public static Gym createGym() {
        return createGym("Gym-" + UUID.randomUUID());
    }

    public static Gym createGym(String name) {
        return Gym.builder()
                .name(name)
                .gymAddress(GymAddress.builder()
                        .country(Country.POLAND)
                        .city("City")
                        .postalCode("11-111")
                        .street("Street")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();
    }

    public static GymCreateRequestDto createRequestDto() {
       return createRequestDto("Gym-" + UUID.randomUUID());
    }

    public static GymCreateRequestDto createRequestDto(String name) {
        return GymCreateRequestDto.builder()
                .name(name)
                .gymAddress(GymAddressRequestDto.builder()
                        .country(Country.POLAND)
                        .city("City")
                        .postalCode("11-111")
                        .street("Street")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();
    }
}