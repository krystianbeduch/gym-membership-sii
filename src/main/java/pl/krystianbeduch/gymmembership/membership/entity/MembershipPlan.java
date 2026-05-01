package pl.krystianbeduch.gymmembership.membership.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "membership_plans")
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    // @NotBlank
    // @Size 100
    private String name;

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    // @NotNull
    private MembershipPlanType type;

    @Embedded
    // @Valid
    // @Size 20
    private Money monthlyPrice;

    // Not Null
    // Positive
    // Max(60)
    @Column(nullable = false)
    private Integer durationInMonths;

    // Not Null
    // Positive
    // Max(999)
    @Column(nullable = false)
    private Integer maxMembers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @OneToMany(mappedBy = "membershipPlan")
    private List<Member> members = new ArrayList<>();
}