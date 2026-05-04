package pl.krystianbeduch.gymmembership.membership.entity;

import jakarta.persistence.*;
import lombok.*;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "membership_plans")
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private MembershipPlanType type;

    @Embedded
    private Money monthlyPrice;

    @Column(nullable = false)
    private Integer durationInMonths;

    @Column(nullable = false)
    private Integer maxMembers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Builder.Default
    @OneToMany(mappedBy = "membershipPlan")
    private List<Member> members = new ArrayList<>();
}