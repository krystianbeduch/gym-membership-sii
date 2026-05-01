package pl.krystianbeduch.gymmembership.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;

import java.util.List;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findAllByGymId(Long gymId);
}