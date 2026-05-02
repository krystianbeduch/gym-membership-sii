package pl.krystianbeduch.gymmembership.membership.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.service.GymService;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.mapper.MembershipPlanMapper;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;
    private final GymService gymService;

    @Transactional
    public MembershipPlanResponseDto createMembershipPlan(
            Long gymId,
            MembershipPlanCreateRequestDto requestDto
    ) {
        log.info(
                "Creating membership plan with name={} for gymId={}",
                requestDto.name(), gymId
        );

        Gym gym = gymService.getGymById(gymId);
        MembershipPlan membershipPlan = membershipPlanMapper.requestDtoToEntity(requestDto);
        membershipPlan.setGym(gym);

        MembershipPlan savedMembershipPlan = membershipPlanRepository.save(membershipPlan);

        log.info(
                "Membership plan created successfully. id={}, name={}, gymId={}",
                savedMembershipPlan.getId(),
                savedMembershipPlan.getName(),
                gymId
        );

        return membershipPlanMapper.entityToResponseDto(savedMembershipPlan);
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanResponseDto> getAllMembershipPlanForGym(
            Long gymId
    ) {
        log.info("Fetching all membership plans for gymId={}", gymId);

        gymService.validateGymExists(gymId);

        List<MembershipPlanResponseDto> membershipPlans = membershipPlanRepository
                .findAllByGymId(gymId)
                .stream()
                .map(membershipPlanMapper::entityToResponseDto)
                .toList();

        log.info(
                "Fetched {} membership plans for gymId={}",
                membershipPlans.size(), gymId
        );
        return membershipPlans;
    }
}