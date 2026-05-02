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
        return new Gym(
                name,
                new GymAddress(
                        Country.POLAND,
                        "City",
                        "11-111",
                        "Street",
                        "1",
                        null
                ),
                "123"
        );
    }

    public static GymCreateRequestDto createRequestDto() {
       return createRequestDto("Gym-" + UUID.randomUUID());
    }

    public static GymCreateRequestDto createRequestDto(String name) {
        return new GymCreateRequestDto(
                name,
                new GymAddressRequestDto(
                        Country.POLAND,
                        "City",
                        "11-111",
                        "Street",
                        "1",
                        null
                ),
                "123"
        );
    }
}