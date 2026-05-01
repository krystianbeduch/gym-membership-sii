package pl.krystianbeduch.gymmembership.membership.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.service.MembershipPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/gyms/{gymId}/membership-plans")
@RequiredArgsConstructor
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipPlanResponseDto createMembershipPlan(
            @PathVariable("gymId") Long gymId,
            @Valid @RequestBody MembershipPlanCreateRequestDto requestDto
    ) {
        return membershipPlanService.createMembershipPlan(gymId, requestDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MembershipPlanResponseDto> getAllMembershipPlansForGym(
            @PathVariable Long gymId
    ) {
        return membershipPlanService.getAllMembershipPlanForGym(gymId);
    }
}
