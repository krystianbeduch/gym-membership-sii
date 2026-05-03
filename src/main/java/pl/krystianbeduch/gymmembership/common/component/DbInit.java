package pl.krystianbeduch.gymmembership.common.component;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.entity.GymAddress;
import pl.krystianbeduch.gymmembership.gym.enums.Country;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DbInit implements CommandLineRunner {

    private final GymRepository gymRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (gymRepository.existsByName("Gym")) {
            return;
        }

        Gym pureGym = gymRepository.save(new Gym(
                "PureGym London Angel",
                new GymAddress(
                        Country.UNITED_KINGDOM,
                        "London",
                        "N1 0QH",
                        "Upper St",
                        "52",
                        null
                ),
                "+44 111-222-333"
        ));

        Gym mcFitGym = gymRepository.save(new Gym(
                "McFIT Fitnessstudio Berlin-Stadtmitte",
                new GymAddress(
                        Country.GERMANY,
                        "Berlin",
                        "10117",
                        "Lepizig Str.",
                        "46/47",
                        null
                ),
                "+49 444-555-666"
        ));

        Gym fabrykaFormyGym = gymRepository.save(new Gym(
                "Fabryka Formy Katowice KTW",
                new GymAddress(
                        Country.POLAND,
                        "Katowice",
                        "40-203",
                        "al. Rozdzienskiego.",
                        "1",
                        null
                ),
                "+89 777-888-999"
        ));

        MembershipPlan pureStarter = new MembershipPlan(
                "Pure Starter",
                MembershipPlanType.BASIC,
                new Money(
                        new BigDecimal("34.99"),
                        Currency.getInstance("EUR")
                ),
                1,
                100,
                pureGym
        );

        MembershipPlan mcBasic = new MembershipPlan(
            "McBasic",
            MembershipPlanType.BASIC,
            new Money(
                    new BigDecimal("22.99"),
                    Currency.getInstance("GBP")
            ),
            1,
            250,
            mcFitGym
        );

        MembershipPlan mcPremium = new MembershipPlan(
            "McPremium",
            MembershipPlanType.PREMIUM,
            new Money(
                    new BigDecimal("143.49"),
                    Currency.getInstance("EUR")
            ),
            3,
            60,
            mcFitGym
        );

        MembershipPlan mcElite = new MembershipPlan(
            "McElite",
            MembershipPlanType.PREMIUM,
            new Money(
                    new BigDecimal("999.99"),
                    Currency.getInstance("PLN")
            ),
            6,
            60,
            mcFitGym
        );

        MembershipPlan fabrykaDuo = new MembershipPlan(
            "FabrykaDuo",
            MembershipPlanType.GROUP,
            new Money(
                    new BigDecimal("79.29"),
                    Currency.getInstance("PLN")
            ),
            1,
            2,
            fabrykaFormyGym
        );

        membershipPlanRepository.saveAll(List.of(
                pureStarter, mcBasic, mcPremium, mcElite, fabrykaDuo
        ));

        List<Member> members = List.of(
            new Member(
                    "Oliver",
                    "Bennett",
                    "oliver.bennett@example.com",
                    MemberStatus.ACTIVE,
                    pureStarter
            ),
            new Member(
                    "Amelia",
                    "Carter",
                    "amelia.carter@example.com",
                    MemberStatus.ACTIVE,
                    pureStarter
            ),
            new Member(
                    "Ethan",
                    "Price",
                    "ethan.price@example.com",
                    MemberStatus.CANCELLED,
                    pureStarter
            ),

            new Member(
                    "Lukas",
                    "Neumann",
                    "lukas.neumann@example.com",
                    MemberStatus.ACTIVE,
                    mcBasic
            ),
            new Member(
                    "Hannah",
                    "Fischer",
                    "hannah.fischer@example.com",
                    MemberStatus.ACTIVE,
                    mcBasic
            ),
            new Member(
                    "Jonas",
                    "Vogel",
                    "jonas.vogel@example.com",
                    MemberStatus.CANCELLED,
                    mcBasic
            ),

            new Member(
                    "Mila",
                    "Schneider",
                    "mila.schneider@example.com",
                    MemberStatus.ACTIVE,
                    mcPremium
            ),
            new Member(
                    "Noah",
                    "Krause",
                    "noah.krause@example.com",
                    MemberStatus.ACTIVE,
                    mcPremium
            ),
            new Member(
                    "Leonie",
                    "Hartmann",
                    "leonie.hartmann@example.com",
                    MemberStatus.CANCELLED,
                    mcPremium
            ),

            new Member(
                    "Kacper",
                    "Nowicki",
                    "kacper.nowicki@example.com",
                    MemberStatus.ACTIVE,
                    mcElite
            ),
            new Member(
                    "Zuzanna",
                    "Wrobel",
                    "zuzanna.wrobel@example.com",
                    MemberStatus.ACTIVE,
                    mcElite
            ),
            new Member(
                    "Mateusz",
                    "Jablonski",
                    "mateusz.jablonski@example.com",
                    MemberStatus.CANCELLED,
                    mcElite
            ),

            new Member(
                    "Jakub",
                    "Kowalczyk",
                    "jakub.kowalczyk@example.com",
                    MemberStatus.ACTIVE,
                    fabrykaDuo
            ),
            new Member(
                    "Maja",
                    "Szymanska",
                    "maja.szymanska@example.com",
                    MemberStatus.CANCELLED,
                    fabrykaDuo
            )
        );

        memberRepository.saveAll(members);
    }
}