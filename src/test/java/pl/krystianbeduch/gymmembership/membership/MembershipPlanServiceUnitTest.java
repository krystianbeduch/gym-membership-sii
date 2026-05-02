package pl.krystianbeduch.gymmembership.membership;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.exception.GymNotFoundException;
import pl.krystianbeduch.gymmembership.gym.service.GymService;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.mapper.MembershipPlanMapper;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipPlanServiceUnitTest {

    @Mock
    private MembershipPlanRepository membershipPlanRepository;

    @Mock
    private MembershipPlanMapper membershipPlanMapper;

    @Mock
    private GymService gymService;

    @InjectMocks
    private MembershipPlanService membershipPlanService;

    @Test
    void createMembershipPlan_shouldCreateMembershipPlanWhenGymExists() {
        Long gymId = 1L;
        Gym gym = new Gym(1L, "Gym");
        MembershipPlan membershipPlan = new MembershipPlan(
                "Premium 6M",
                MembershipPlanType.PREMIUM
        );
        MembershipPlan savedMembershipPlan = new MembershipPlan(
                "Premium 6M",
                MembershipPlanType.PREMIUM
        );
        savedMembershipPlan.setId(2L);
        savedMembershipPlan.setGym(gym);

        MembershipPlanCreateRequestDto requestDto = MembershipPlanTestDataFactory.createRequestDto();
        MembershipPlanResponseDto responseDto = MembershipPlanTestDataFactory.createResponseDto(10L);

        when(gymService.getGymById(gymId))
                .thenReturn(gym);
        when(membershipPlanMapper.requestDtoToEntity(requestDto))
                .thenReturn(membershipPlan);
        when(membershipPlanRepository.save(membershipPlan))
                .thenReturn(savedMembershipPlan);
        when(membershipPlanMapper.entityToResponseDto(savedMembershipPlan))
                .thenReturn(responseDto);

        MembershipPlanResponseDto result = membershipPlanService.createMembershipPlan(
                gymId, requestDto
        );

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(1L, result.gymId());
        assertEquals("Gym", result.gymName());
        assertEquals("Premium 6M", result.name());
        assertEquals(MembershipPlanType.PREMIUM, result.type());
        assertEquals(new BigDecimal("999.99"), result.monthlyPriceAmount());
        assertEquals("PLN", result.monthlyPriceCurrencyCode());
        assertEquals(6, result.durationInMonths());
        assertEquals(100, result.maxMembers());

        verify(gymService).getGymById(gymId);
        verify(membershipPlanMapper).requestDtoToEntity(requestDto);
        verify(membershipPlanRepository).save(membershipPlan);
        verify(membershipPlanMapper).entityToResponseDto(savedMembershipPlan);
        verifyNoMoreInteractions(gymService, membershipPlanMapper, membershipPlanRepository);
    }

    @Test
    void createMembershipPlan_shouldThrowGymNotFoundExceptionWhenGymDoesNotExist() {
        Long gymId = 99L;
        MembershipPlanCreateRequestDto requestDto = MembershipPlanTestDataFactory.createRequestDto();

        when(gymService.getGymById(gymId))
            .thenThrow(new GymNotFoundException("Gym with id " + gymId + " not found"));

        GymNotFoundException exception = assertThrows(
                GymNotFoundException.class,
                () -> membershipPlanService.createMembershipPlan(gymId, requestDto)
        );

        assertEquals("Gym with id " + gymId + " not found", exception.getMessage());

        verify(gymService).getGymById(gymId);
        verifyNoInteractions(membershipPlanMapper);
        verify(membershipPlanRepository, never()).save(any());
    }

    @Test
    void getAllMembershipPlanForGym_shouldReturnAllPlansWhenGymExists() {
        Long gymId = 1L;

        MembershipPlan plan1 = new MembershipPlan();
        plan1.setId(11L);
        plan1.setName("Basic 1M");

        MembershipPlan plan2 = new MembershipPlan();
        plan2.setId(12L);
        plan2.setName("Premium 6M");

        MembershipPlanResponseDto dto1 = MembershipPlanTestDataFactory.createResponseDto(11L);
        MembershipPlanResponseDto dto2 = MembershipPlanTestDataFactory.createResponseDto(12L);

        doNothing().when(gymService).validateGymExists(gymId);
        when(membershipPlanRepository.findAllByGymId(gymId))
                .thenReturn(List.of(plan1, plan2));
        when(membershipPlanMapper.entityToResponseDto(plan1))
                .thenReturn(dto1);
        when(membershipPlanMapper.entityToResponseDto(plan2))
                .thenReturn(dto2);

        List<MembershipPlanResponseDto> result = membershipPlanService.getAllMembershipPlanForGym(gymId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(gymService).validateGymExists(gymId);
        verify(membershipPlanRepository).findAllByGymId(gymId);
        verify(membershipPlanMapper).entityToResponseDto(plan1);
        verify(membershipPlanMapper).entityToResponseDto(plan2);
        verifyNoMoreInteractions(gymService, membershipPlanMapper, membershipPlanRepository);
    }

    @Test
    void getAllMembershipPlanForGym_shouldThrowGymNotFoundExceptionWhenGymDoesNotExist() {
        Long gymId = 99L;

        doThrow(new GymNotFoundException("Gym with id " + gymId + " not found"))
                .when(gymService).validateGymExists(gymId);

        GymNotFoundException exception = assertThrows(
                GymNotFoundException.class,
                () -> membershipPlanService.getAllMembershipPlanForGym(gymId)
        );

        assertEquals("Gym with id " + gymId + " not found", exception.getMessage());

        verify(gymService).validateGymExists(gymId);
        verifyNoInteractions(membershipPlanRepository, membershipPlanMapper);
    }
}