package pl.krystianbeduch.gymmembership.gym.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.service.GymService;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public GymResponseDto createGym(@Valid @RequestBody GymCreateRequestDto request) {
        return gymService.createGym(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GymResponseDto> getAllGyms() {
        return gymService.getAllGyms();
    }
}