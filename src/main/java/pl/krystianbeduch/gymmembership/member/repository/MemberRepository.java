package pl.krystianbeduch.gymmembership.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    long countByMembershipPlanIdAndMemberStatus(
            Long membershipPlanId, MemberStatus memberStatus
    );
}