package pl.krystianbeduch.gymmembership.membership.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanCreateRequestDto;
import pl.krystianbeduch.gymmembership.membership.dto.MembershipPlanResponseDto;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MembershipPlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gym", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "monthlyPrice.amount", source = "monthlyPriceAmount")
    @Mapping(target = "monthlyPrice.currencyCode", source = "monthlyPriceCurrencyCode")
    MembershipPlan requestDtoToEntity(MembershipPlanCreateRequestDto request);

    @Mapping(target = "gymId", source = "gym.id")
    @Mapping(target = "gymName", source = "gym.name")
    @Mapping(target = "monthlyPriceAmount", source = "monthlyPrice.amount")
    @Mapping(target = "monthlyPriceCurrencyCode", source = "monthlyPrice.currencyCode")
    MembershipPlanResponseDto entityToResponseDto(MembershipPlan membershipPlan);
}