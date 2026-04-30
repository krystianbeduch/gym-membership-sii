package pl.krystianbeduch.gymmembership.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.krystianbeduch.gymmembership.enums.Country;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class GymAddress {

    @NotNull(message = "{gym.address.country.notNull}")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Country country;

    @NotBlank(message = "{gym.address.city.notBlank}")
    @Size(max = 60, message = "{gym.address.city.size}")
    @Column(nullable = false, length = 60)
    private String city;

    @NotBlank(message = "{gym.address.postalCode.notBlank}")
    @Size(max = 15, message = "{gym.address.postalCode.size}")
    @Column(nullable = false, length = 15)
    private String postalCode;

    @NotBlank(message = "{gym.address.street.notBlank}")
    @Size(max = 60, message = "{gym.address.street.size}")
    @Column(nullable = false, length = 60)
    private String street;

    @NotBlank(message = "{gym.address.buildingNumber.notBlank}")
    @Size(max = 15, message = "{gym.address.buildingNumber.size}")
    @Column(nullable = false, length = 15)
    private String buildingNumber;

    @Size(max = 15, message = "{gym.address.apartmentNumber.size}")
    @Column(length = 15)
    private String apartmentNumber;
}