package pl.krystianbeduch.gymmembership.membership;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.krystianbeduch.gymmembership.membership.controller.MembershipPlanController;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MembershipPlanController.class)
class MembershipPlanControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MembershipPlanService membershipPlanService;

    private final String API_URL = "/api/gyms/{gymId}/membership-plans";

    @Test
    void createMembershipPlan_shouldReturn201AndBody_whenRequestIsValid() throws Exception {
        Long gymId = 1L;
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto();
        MembershipPlanResponseDto response = MembershipPlanTestDataFactory.createResponseDto(10L);

        when(membershipPlanService.createMembershipPlan(
                eq(gymId), any(MembershipPlanCreateRequestDto.class))
        ).thenReturn(response);

        mockMvc.perform(post(API_URL, gymId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.gymId").value(1))
                .andExpect(jsonPath("$.gymName").value("Gym"))
                .andExpect(jsonPath("$.name").value("Premium 6M"))
                .andExpect(jsonPath("$.type").value("PREMIUM"))
                .andExpect(jsonPath("$.monthlyPriceAmount").value(999.99))
                .andExpect(jsonPath("$.monthlyPriceCurrencyCode").value("PLN"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.maxMembers").value(100));

        verify(membershipPlanService).createMembershipPlan(
                eq(gymId), any(MembershipPlanCreateRequestDto.class)
        );
    }

    @Test
    void createMembershipPlan_shouldReturn400_whenRequestBodyIsInvalid() throws Exception {
        Long gymId = 1L;
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto("  ");

        mockMvc.perform(post(API_URL, gymId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(membershipPlanService, never()).createMembershipPlan(
                eq(gymId), any()
        );
    }

    @Test
    void getAllMembershipPlansForGym_shouldReturn200AndList() throws Exception {
        Long gymId = 1L;

        List<MembershipPlanResponseDto> response = List.of(
                MembershipPlanTestDataFactory.createResponseDto(10L),
                MembershipPlanTestDataFactory.createResponseDto(11L)
        );

        when(membershipPlanService.getAllMembershipPlanForGym(gymId))
                .thenReturn(response);

        mockMvc.perform(get(API_URL, gymId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].gymId").value(1))
                .andExpect(jsonPath("$[0].name").value("Premium 6M"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].gymId").value(1))
                .andExpect(jsonPath("$[1].name").value("Premium 6M"));

        verify(membershipPlanService).getAllMembershipPlanForGym(gymId);
    }

    @Test
    void createMembershipPlan_shouldReturn400_whenGymIdIsNegative() throws Exception {
        MembershipPlanCreateRequestDto request = MembershipPlanTestDataFactory.createRequestDto();

        mockMvc.perform(post(API_URL, -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(membershipPlanService, never()).createMembershipPlan(any(), any());
    }

    @Test
    void getAllMembershipPlansForGym_shouldReturn400_whenGymIdIsNegative() throws Exception {
        mockMvc.perform(get(API_URL, -1))
                .andExpect(status().isBadRequest());

        verify(membershipPlanService, never()).getAllMembershipPlanForGym(any());
    }
}