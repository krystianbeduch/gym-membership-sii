package pl.krystianbeduch.gymmembership.member.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(
            path = "/api/membership-plans/{membershipPlanId}/members",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponseDto registerMemberToMembershipPlan(
            @PathVariable("membershipPlanId")
            @Positive(message = "{membershipPlan.id.positive}")
            Long membershipPlanId,
            @Valid @RequestBody MemberRegisterToMembershipRequestDto requestDto
    ) {
        return memberService.registerMemberToMembershipPlan(
                membershipPlanId, requestDto
        );
    }

    @GetMapping(
            path = "/api/members",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<MemberResponseDto> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping(
            path = "/api/members/{memberId}/cancel-membership",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public MemberResponseDto cancelMembership(
            @PathVariable("memberId")
            @Positive(message = "{member.id.positive}")
            Long memberId
    ) {
        return memberService.cancelMembership(memberId);
    }
}