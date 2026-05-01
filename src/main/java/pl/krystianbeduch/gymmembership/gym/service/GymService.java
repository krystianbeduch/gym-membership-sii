package pl.krystianbeduch.gymmembership.gym.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.exception.GymNameAlreadyExistsException;
import pl.krystianbeduch.gymmembership.gym.mapper.GymMapper;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GymService {

    private final GymRepository gymRepository;
    private final GymMapper gymMapper;

    @Transactional
    public GymResponseDto createGym(GymCreateRequestDto request) {
        log.info("Creating gym with name={}", request.name());

        if (gymRepository.existsByName(request.name())) {
            log.warn(
                    "Gym creation failed. Gym with name={} already exists",
                    request.name()
            );
            throw new GymNameAlreadyExistsException(
                    "Gym with name '" + request.name() + "' already exists"
            );
        }

        Gym gym = gymMapper.requestDtoToEntity(request);
        Gym savedGym = gymRepository.save(gym);

        log.info(
                "Gym created successfully. id={}, name={}",
                savedGym.getId(), savedGym.getName()
        );

        return gymMapper.entityToResponseDto(savedGym);
    }

    @Transactional(readOnly = true)
    public List<GymResponseDto> getAllGyms() {
        log.info("Fetching all gyms");

        List<GymResponseDto> gyms = gymRepository.findAll()
                .stream()
                .map(gymMapper::entityToResponseDto)
                .toList();

        log.debug("Fetched {} gyms", gyms.size());
        return gyms;
    }
}