package pl.krystianbeduch.gymmembership.gym.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Embedded
    private GymAddress gymAddress;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Builder.Default
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    private List<MembershipPlan> membershipPlan = new ArrayList<>();
}