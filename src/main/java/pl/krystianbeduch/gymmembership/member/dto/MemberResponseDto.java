package pl.krystianbeduch.gymmembership.member.dto;

import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

import java.time.LocalDate;

public record MemberResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate membershipStartDate,
        MemberStatus memberStatus,
        Long membershipPlanId,
        String membershipPlanName,
        Long gymId,
        String gymName
) { }