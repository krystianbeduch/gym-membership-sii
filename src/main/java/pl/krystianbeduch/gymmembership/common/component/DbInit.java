package pl.krystianbeduch.gymmembership.common.component;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;
import pl.krystianbeduch.gymmembership.gym.enums.Country;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;

import java.math.BigDecimal;
import java.util.Currency;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DbInit implements CommandLineRunner {

    private final GymRepository gymRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    @Override
    public void run(String... args) {
        if (gymRepository.existsByName("Gym")) {
            return;
        }

        Gym gym = gymRepository.save(new Gym(
                "Gym",
                new GymAddress(
                        Country.POLAND,
                        "City",
                        "11-111",
                        "Street",
                        "1",
                        null
                ),
                "123"
        ));

        MembershipPlan membershipPlan = new MembershipPlan();
        membershipPlan.setGym(gym);
        membershipPlan.setName("Premium 6M");
        membershipPlan.setType(MembershipPlanType.PREMIUM);
        membershipPlan.setMonthlyPrice(new Money(
                new BigDecimal("999.99"),
                Currency.getInstance("PLN")
        ));
        membershipPlan.setDurationInMonths(6);
        membershipPlan.setMaxMembers(100);

        membershipPlanRepository.save(membershipPlan);
    }
}