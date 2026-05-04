package pl.krystianbeduch.gymmembership.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.krystianbeduch.gymmembership.gym.enums.Country;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@Embeddable
public class GymAddress {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 15)
    private String postalCode;

    @Column(nullable = false, length = 60)
    private String street;

    @Column(nullable = false, length = 15)
    private String buildingNumber;

    @Size(max = 15, message = "{gym.address.apartmentNumber.size}")
    @Column(length = 15)
    private String apartmentNumber;
}