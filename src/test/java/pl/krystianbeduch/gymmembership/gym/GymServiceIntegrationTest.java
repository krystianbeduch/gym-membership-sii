package pl.krystianbeduch.gymmembership.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;
import pl.krystianbeduch.gymmembership.gym.enums.Country;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.gym.service.GymService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class GymServiceIntegrationTest {

    @Autowired
    private GymService gymService;

    @Autowired
    private GymRepository gymRepository;

    @BeforeEach
    void setUp() {
        gymRepository.deleteAll();
    }

    @Test
    void createGym_shouldPersistGymWhenNameIsUnique() {
        GymCreateRequestDto request = createRequestDto();

        GymResponseDto result = gymService.createGym(request);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Gym", result.name());
        assertEquals(1, gymRepository.count());

        Gym savedGym = gymRepository.findAll().getFirst();
        assertEquals("Gym", savedGym.getName());
    }

    @Test
    void createGym_shouldThrowExceptionWhenGymNameAlreadyExists() {
        gymRepository.save(createEntity("Gym"));

        GymCreateRequestDto request = createRequestDto();

        assertThrows(
                GymNameAlreadyExistsException.class,
                () -> gymService.createGym(request)
        );

        assertEquals(1, gymRepository.count());
    }

    @Test
    void getAllGyms_shouldReturnAllPersistedGyms() {
        Gym gym1 = createEntity("Gym1");
        Gym gym2 = createEntity("Gym2");

        gymRepository.save(gym1);
        gymRepository.save(gym2);

        List<GymResponseDto> result = gymService.getAllGyms();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.name().equals("Gym1")));
        assertTrue(result.stream().anyMatch(g -> g.name().equals("Gym2")));
    }

    private GymCreateRequestDto createRequestDto() {
        return new GymCreateRequestDto(
                "Gym",
                new GymAddressRequestDto(
                        Country.POLAND,
                        "City",
                        "11-111",
                        "Street1",
                        "1",
                        null
                ),
                "123"
        );
    }

    private Gym createEntity(String name) {
        return new Gym(
                name,
                new GymAddress(
                        Country.POLAND,
                        "City",
                        "11-111",
                        "Street1",
                        "1",
                        null
                ),
                "123"
        );
    }
}