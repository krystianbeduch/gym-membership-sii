package pl.krystianbeduch.gymmembership.gym;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.krystianbeduch.gymmembership.gym.controller.GymController;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.service.GymService;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GymController.class)
class GymControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymService gymService;

    private final String API_URL = "/api/gyms";

    @Test
    void createGym_shouldReturn201AndBody_whenRequestIsValid() throws Exception {
        String uniqueName = "Gym-" + UUID.randomUUID();
        GymCreateRequestDto request = GymTestDataFactory.createRequestDto(uniqueName);
        GymResponseDto response = new GymResponseDto(1L, uniqueName, null, "123");

        when(gymService.createGym(any(GymCreateRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(uniqueName));

        verify(gymService).createGym(any(GymCreateRequestDto.class));
    }

    @Test
    void createGym_shouldReturn400_whenRequestIsInvalid() throws Exception {
        GymCreateRequestDto invalidRequest = GymTestDataFactory.createRequestDto("  ");

        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(gymService, never()).createGym(any());
    }

    @Test
    void getAllGyms_shouldReturn200AndListOfGyms() throws Exception {
        when(gymService.getAllGyms()).thenReturn(List.of(
                new GymResponseDto(1L, "Gym-1", null, "123"),
                new GymResponseDto(2L, "Gym-2", null, "456")
        ));

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gym-1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Gym-2"));

        verify(gymService).getAllGyms();
    }
}