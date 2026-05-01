package pl.krystianbeduch.gymmembership.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank(message = "{gym.name.notBlank}")
//    @Size(max = 100, message = "{gym.name.size}")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

//    @NotNull(message = "{gym.address.notNull}")
//    @Valid
    @Embedded
    private GymAddress gymAddress;

//    @NotBlank(message = "{gym.phoneNumber.notBlank}")
//    @Size(max = 20, message = "{gym.phoneNumber.size}")
    @Column(nullable = false, length = 20)
    private String phoneNumber;
}