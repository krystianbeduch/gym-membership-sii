package pl.krystianbeduch.gymmembership.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.krystianbeduch.gymmembership.member.controller.MemberController;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.service.MemberService;
import pl.krystianbeduch.gymmembership.testdata.MemberTestDataFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    private static final String REGISTER_MEMBER_API_URL = "/api/membership-plans/{membershipPlanId}/members";
    private static final String GET_ALL_MEMBERS_API_URL = "/api/members";
    private static final String CANCEL_MEMBERSHIP_API_URL = "/api/members/{memberId}/cancel-membership";

    @Test
    void registerMemberToMembershipPlan_shouldReturn200AndBody_whenRequestIsValid() throws Exception {
        Long membershipPlanId = 1L;
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto();
        MemberResponseDto response = MemberTestDataFactory.createMemberResponseDto();

        when(memberService.registerMemberToMembershipPlan(
                eq(membershipPlanId), any(MemberRegisterToMembershipRequestDto.class))
        ).thenReturn(response);

        mockMvc.perform(post(REGISTER_MEMBER_API_URL, membershipPlanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.memberStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.membershipPlanId").value(10))
                .andExpect(jsonPath("$.membershipPlanName").value("Premium Plan"))
                .andExpect(jsonPath("$.gymId").value(100))
                .andExpect(jsonPath("$.gymName").value("Gym"));

        verify(memberService).registerMemberToMembershipPlan(
                eq(membershipPlanId), any(MemberRegisterToMembershipRequestDto.class)
        );
    }

    @Test
    void registerMemberToMembershipPlan_shouldReturn400_whenRequestBodyIsInvalid() throws Exception {
        Long membershipPlanId = 1L;
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto("  ");

        mockMvc.perform(post(REGISTER_MEMBER_API_URL, membershipPlanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).registerMemberToMembershipPlan(
                eq(membershipPlanId), any()
        );
    }

    @Test
    void registerMemberToMembershipPlan_shouldReturn400_whenMembershipPlanIdIsNegative() throws Exception {
        MemberRegisterToMembershipRequestDto request =
                MemberTestDataFactory.createMemberRegisterRequestDto();

        mockMvc.perform(post(REGISTER_MEMBER_API_URL, -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).registerMemberToMembershipPlan(any(), any());
    }

    @Test
    void getAllMembers_shouldReturn200AndList() throws Exception {
        List<MemberResponseDto> response = List.of(
                MemberTestDataFactory.createMemberResponseDto(
                        1L, "john.doe@example.com"
                ),
                MemberTestDataFactory.createMemberResponseDto(
                        2L, "jan.kowalski@example.com"
                )
        );

        when(memberService.getAllMembers()).thenReturn(response);

        mockMvc.perform(get(GET_ALL_MEMBERS_API_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].memberStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("jan.kowalski@example.com"))
                .andExpect(jsonPath("$[1].memberStatus").value("ACTIVE"));

        verify(memberService).getAllMembers();
    }

    @Test
    void cancelMembership_shouldReturn200AndBody_whenMemberIdIsValid() throws Exception {
        Long memberId = 1L;
        MemberResponseDto response = MemberTestDataFactory.createMemberResponseDto(
                memberId, "john.doe@example.com"
        );

        when(memberService.cancelMembership(memberId)).thenReturn(response);

        mockMvc.perform(post(CANCEL_MEMBERSHIP_API_URL, memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(memberService).cancelMembership(memberId);
    }

    @Test
    void cancelMembership_shouldReturn400_whenMemberIdIsNegative() throws Exception {
        mockMvc.perform(post(CANCEL_MEMBERSHIP_API_URL, -1))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).cancelMembership(any());
    }
}