package pl.krystianbeduch.gymmembership.gym.mapper;

import org.mapstruct.Mapper;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymAddressResponseDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymCreateRequestDto;
import pl.krystianbeduch.gymmembership.gym.dto.GymResponseDto;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;

@Mapper(componentModel = "spring")
public interface GymMapper {

    Gym requestDtoToEntity(GymCreateRequestDto request);
    GymAddress requestDtoToEntity(GymAddressRequestDto request);

    GymResponseDto entityToResponseDto(Gym gym);
    GymAddressResponseDto entityToResponseDto(GymAddress gymAddress);
}