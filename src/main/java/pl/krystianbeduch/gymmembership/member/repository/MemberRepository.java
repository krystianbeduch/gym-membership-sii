package pl.krystianbeduch.gymmembership.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailAndMemberStatus(String email, MemberStatus memberStatus);

    long countByMembershipPlanIdAndMemberStatus(
            Long membershipPlanId, MemberStatus memberStatus
    );

    List<Member> findAllByMemberStatus(MemberStatus memberStatus);
}