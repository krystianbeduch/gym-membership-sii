package pl.krystianbeduch.gymmembership.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String firstName;

    @Column(nullable = false, length = 64)
    private String lastName;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate membershipStartDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private MemberStatus memberStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlan membershipPlan;

    public Member(String firstName, String lastName, String email, MemberStatus memberStatus, MembershipPlan membershipPlan) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberStatus = memberStatus;
        this.membershipPlan = membershipPlan;
    }

    public void cancelMembership() {
        memberStatus = MemberStatus.CANCELLED;
    }
}