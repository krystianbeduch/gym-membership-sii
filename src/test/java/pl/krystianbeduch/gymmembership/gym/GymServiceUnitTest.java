package pl.krystianbeduch.gymmembership.gym;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressResponseDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.enums.Country;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;
import pl.krystianbeduch.gymmembership.gym.mapper.GymMapper;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.gym.service.GymService;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymServiceUnitTest {

    @Mock
    private GymRepository gymRepository;

    @Mock
    private GymMapper gymMapper;

    @InjectMocks
    private GymService gymService;

    @Test
    void createGym_shouldCreateGymWhenNameIsUnique() {
        GymCreateRequestDto requestDto = GymTestDataFactory.createRequestDto();

        Gym gym = new Gym();
        Gym savedGym = Gym.builder()
                .id(1L)
                .name("Gym")
                .build();
        GymResponseDto responseDto = GymResponseDto.builder()
                .id(1L)
                .name("Gym")
                .build();

        when(gymRepository.existsByName(requestDto.name()))
                .thenReturn(false);
        when(gymMapper.requestDtoToEntity(requestDto))
                .thenReturn(gym);
        when(gymRepository.save(gym))
                .thenReturn(savedGym);
        when(gymMapper.entityToResponseDto(savedGym))
                .thenReturn(responseDto);

        GymResponseDto result = gymService.createGym(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Gym", result.name());

        verify(gymRepository).existsByName(requestDto.name());
        verify(gymMapper).requestDtoToEntity(requestDto);
        verify(gymRepository).save(gym);
        verifyNoMoreInteractions(gymRepository, gymMapper);
    }

    @Test
    void createGym_shouldThrowGymNameAlreadyExistsExceptionWhenNameExists() {
        GymCreateRequestDto requestDto = GymTestDataFactory.createRequestDto();

        when(gymRepository.existsByName(requestDto.name()))
                .thenReturn(true);

        GymNameAlreadyExistsException exception = assertThrows(
                GymNameAlreadyExistsException.class,
                () -> gymService.createGym(requestDto)
        );

        assertEquals(
                "Gym with name '" + requestDto.name() + "' already exists",
                exception.getMessage()
        );
        verify(gymRepository).existsByName(requestDto.name());
        verifyNoInteractions(gymMapper);
        verify(gymRepository, never()).save(any());
    }

    @Test
    void getAllGyms_shouldReturnAllGyms() {
        Gym gym1 = Gym.builder().id(1L).name("Gym-1").build();
        Gym gym2 = Gym.builder().id(2L).name("Gym-2").build();

        GymResponseDto response1 = GymResponseDto.builder()
                .id(1L)
                .name("Gym-1")
                .gymAddress(GymAddressResponseDto.builder()
                        .country(Country.POLAND)
                        .city("City1")
                        .postalCode("11-111")
                        .street("Street1")
                        .buildingNumber("1")
                        .build())
                .phoneNumber("123")
                .build();

        GymResponseDto response2 = GymResponseDto.builder()
                .id(2L)
                .name("Gym-2")
                .gymAddress(GymAddressResponseDto.builder()
                        .country(Country.POLAND)
                        .city("City2")
                        .postalCode("22-222")
                        .street("Street2")
                        .buildingNumber("2")
                        .build())
                .phoneNumber("456")
                .build();

        when(gymRepository.findAll())
                .thenReturn(List.of(gym1, gym2));
        when(gymMapper.entityToResponseDto(gym1))
                .thenReturn(response1);
        when(gymMapper.entityToResponseDto(gym2))
                .thenReturn(response2);

        List<GymResponseDto> result = gymService.getAllGyms();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        assertEquals("Gym-1", result.get(0).name());
        assertEquals("Gym-2", result.get(1).name());
        verify(gymRepository).findAll();
        verify(gymMapper).entityToResponseDto(gym1);
        verify(gymMapper).entityToResponseDto(gym2);
        verifyNoMoreInteractions(gymRepository, gymMapper);
    }
}