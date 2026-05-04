package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

public final class MemberTestDataFactory {

    private MemberTestDataFactory() {}

    public static Member createMember(
            String email, MembershipPlan plan
    ) {
        return Member.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .membershipPlan(plan)
                .build();
    }

    public static MemberRegisterToMembershipRequestDto createMemberRegisterRequestDto(
            String firstName
    ) {
        return new MemberRegisterToMembershipRequestDto(
                firstName,
                "Doe",
                "john.doe@example.com"
        );
    }

    public static MemberRegisterToMembershipRequestDto createMemberRegisterRequestDto() {
        return createMemberRegisterRequestDto("John");
    }

    public static MemberResponseDto createMemberResponseDto() {
        return createMemberResponseDto(100L, "john.doe@example.com");
    }

    public static MemberResponseDto createMemberResponseDto(
            Long id,
            String email
    ) {
        return new MemberResponseDto(
                 id,
                "John",
                "Doe",
                email,
                null,
                MemberStatus.ACTIVE,
                10L,
                "Premium Plan",
                100L,
                "Gym"
        );
    }
}