package pl.krystianbeduch.gymmembership.testdata;

import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

public final class MemberTestDataFactory {

    private MemberTestDataFactory() {}

    public static MemberRegisterToMembershipRequestDto createMemberRegisterRequestDto() {
        return new MemberRegisterToMembershipRequestDto(
                "John",
                "Doe",
                "john.doe@example.com"
        );
    }

    public static MemberResponseDto createMemberResponseDto() {
        return new MemberResponseDto(
                 100L,
                "John",
                "Doe",
                "john.doe@example.com",
                null,
                MemberStatus.ACTIVE,
                10L,
                "Premium Plan",
                100L,
                "Gym"
        );
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