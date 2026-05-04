package pl.krystianbeduch.gymmembership.gym;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.gym.service.GymService;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;

import java.util.List;
import java.util.UUID;

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
        String uniqueName = "Gym-" + UUID.randomUUID();
        GymCreateRequestDto request = GymTestDataFactory.createRequestDto(uniqueName);

        GymResponseDto result = gymService.createGym(request);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(uniqueName, result.name());
        assertEquals(1, gymRepository.count());

        Gym savedGym = gymRepository.findAll().getFirst();
        assertEquals(uniqueName, savedGym.getName());
    }

    @Test
    void createGym_shouldThrowExceptionWhenGymNameAlreadyExists() {
        String uniqueName = "Gym-" + UUID.randomUUID();
        gymRepository.save(GymTestDataFactory.createGym(uniqueName));

        GymCreateRequestDto request = GymTestDataFactory.createRequestDto(uniqueName);

        assertThrows(
                GymNameAlreadyExistsException.class,
                () -> gymService.createGym(request)
        );

        assertEquals(1, gymRepository.count());
    }

    @Test
    void getAllGyms_shouldReturnAllPersistedGyms() {
        Gym gym1 = GymTestDataFactory.createGym("Gym1");
        Gym gym2 = GymTestDataFactory.createGym("Gym2");

        gymRepository.save(gym1);
        gymRepository.save(gym2);

        List<GymResponseDto> result = gymService.getAllGyms();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.name().equals("Gym1")));
        assertTrue(result.stream().anyMatch(g -> g.name().equals("Gym2")));
    }
}