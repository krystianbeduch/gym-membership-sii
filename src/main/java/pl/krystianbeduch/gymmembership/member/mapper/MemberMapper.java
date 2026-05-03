package pl.krystianbeduch.gymmembership.member.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.krystianbeduch.gymmembership.member.dto.MemberRegisterToMembershipRequestDto;
import pl.krystianbeduch.gymmembership.member.dto.MemberResponseDto;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = MemberStatus.class
)
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membershipStartDate", ignore = true)
    @Mapping(target = "memberStatus", expression = "java(MemberStatus.ACTIVE)")
    @Mapping(target = "membershipPlan", source = "membershipPlan")
    Member requestDtoToEntity(
            MemberRegisterToMembershipRequestDto request,
            MembershipPlan membershipPlan
    );

    @Mapping(target = "membershipPlanId", source = "membershipPlan.id")
    @Mapping(target = "membershipPlanName", source = "membershipPlan.name")
    @Mapping(target = "gymId", source = "membershipPlan.gym.id")
    @Mapping(target = "gymName", source = "membershipPlan.gym.name")
    MemberResponseDto entityToResponseDto(Member member);
}