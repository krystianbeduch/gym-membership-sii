package pl.krystianbeduch.gymmembership.gym.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressResponseDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface GymMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membershipPlan", ignore = true)
    Gym requestDtoToEntity(GymCreateRequestDto request);
    GymAddress requestDtoToEntity(GymAddressRequestDto request);

    GymResponseDto entityToResponseDto(Gym gym);
    GymAddressResponseDto entityToResponseDto(GymAddress gymAddress);
}