package pl.krystianbeduch.gymmembership.membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.exception.GymNotFoundException;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;
import pl.krystianbeduch.gymmembership.testdata.MembershipPlanTestDataFactory;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class MembershipPlanServiceIntegrationTest {

    @Autowired
    private MembershipPlanService membershipPlanService;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private GymRepository gymRepository;

    @BeforeEach
    void cleanDb() {
        membershipPlanRepository.deleteAll();
        gymRepository.deleteAll();
    }

    @Test
    void createMembershipPlan_shouldCreateMembershipPlanWhenGymExists() {
        String uniqueName = "Gym-" + UUID.randomUUID();
        Gym savedGym = gymRepository.save(
                GymTestDataFactory.createGym(uniqueName)
        );
        MembershipPlanCreateRequestDto requestDto = MembershipPlanTestDataFactory.createRequestDto();
        MembershipPlanResponseDto result = membershipPlanService.createMembershipPlan(savedGym.getId(), requestDto);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(savedGym.getId(), result.gymId());
        assertEquals(uniqueName, result.gymName());
        assertEquals("Premium 6M", result.name());
        assertEquals(MembershipPlanType.PREMIUM, result.type());
        assertEquals(new BigDecimal("999.99"), result.monthlyPriceAmount());
        assertEquals("PLN", result.monthlyPriceCurrencyCode());
        assertEquals(6, result.durationInMonths());
        assertEquals(100, result.maxMembers());

        List<MembershipPlan> plansInDb = membershipPlanRepository.findAllByGymId(savedGym.getId());
        assertEquals(1, plansInDb.size());
        assertEquals("Premium 6M", plansInDb.getFirst().getName());
    }

    @Test
    void createMembershipPlan_shouldThrowGymNotFoundExceptionWhenGymDoesNotExist() {
        Long nonExistingGymId = 99L;
        MembershipPlanCreateRequestDto requestDto = MembershipPlanTestDataFactory.createRequestDto();

        GymNotFoundException exception = assertThrows(
                GymNotFoundException.class,
                () -> membershipPlanService.createMembershipPlan(nonExistingGymId, requestDto)
        );

        assertEquals("Gym with id " + nonExistingGymId + " not found", exception.getMessage());
        assertTrue(membershipPlanRepository.findAll().isEmpty());
    }

    @Test
    void getAllMembershipPlanForGym_shouldReturnAllPlansWhenGymExists() {
        Gym savedGym = gymRepository.save(
                GymTestDataFactory.createGym()
        );

        MembershipPlan plan1 = new MembershipPlan();
        plan1.setName("Basic 1M");
        plan1.setType(MembershipPlanType.BASIC);
        plan1.setMonthlyPrice(new Money(
                new BigDecimal("99.99"), Currency.getInstance("PLN")
        ));
        plan1.setDurationInMonths(1);
        plan1.setMaxMembers(100);
        plan1.setGym(savedGym);

        MembershipPlan plan2 = new MembershipPlan();
        plan2.setName("Premium 12M");
        plan2.setType(MembershipPlanType.PREMIUM);
        plan2.setMonthlyPrice(new Money(
                new BigDecimal("999.99"), Currency.getInstance("PLN")
        ));
        plan2.setDurationInMonths(12);
        plan2.setMaxMembers(200);
        plan2.setGym(savedGym);

        membershipPlanRepository.save(plan1);
        membershipPlanRepository.save(plan2);

        List<MembershipPlanResponseDto> result =
                membershipPlanService.getAllMembershipPlanForGym(savedGym.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(plan -> plan.name().equals("Basic 1M")));
        assertTrue(result.stream().anyMatch(plan -> plan.name().equals("Premium 12M")));
        assertTrue(result.stream().allMatch(plan -> plan.gymId().equals(savedGym.getId())));
    }

    @Test
    void getAllMembershipPlanForGym_shouldThrowGymNotFoundExceptionWhenGymDoesNotExist() {
        Long nonExistingGymId = 999L;

        GymNotFoundException exception = assertThrows(
                GymNotFoundException.class,
                () -> membershipPlanService.getAllMembershipPlanForGym(nonExistingGymId)
        );

        assertEquals("Gym with id " + nonExistingGymId + " not found", exception.getMessage());
    }
}