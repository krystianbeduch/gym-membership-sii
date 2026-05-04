package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

public final class MemberTestDataFactory {

    private MemberTestDataFactory() {}

    public static MemberRegisterToMembershipRequestDto createMemberRegisterRequestDto() {
        return createMemberRegisterRequestDto("John");
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

    public static MemberResponseDto createMemberResponseDto() {
        return createMemberResponseDto(100L, "john.doe@example.com");
    }

    public static MemberResponseDto createMemberResponseDto(
            Long id,
            String email
    ) {
        return MemberResponseDto.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .memberStatus(MemberStatus.ACTIVE)
                .membershipPlanId(10L)
                .membershipPlanName("Premium Plan")
                .gymId(100L)
                .gymName("Gym")
                .build();
    }
}