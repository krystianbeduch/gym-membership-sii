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
        Gym pureGym = Gym.builder()
                .name("PureGym London Angel")
                .gymAddress(
                        GymAddress.builder()
                                .country(Country.UNITED_KINGDOM)
                                .city("London")
                                .postalCode("N1 0QH")
                                .street("Upper St")
                                .buildingNumber("52")
                                .build()
                )
                .phoneNumber("+44 111-222-333")
                .build();

        Gym mcFitGym = Gym.builder()
                .name("McFIT Fitnessstudio Berlin-Stadtmitte")
                .gymAddress(
                        GymAddress.builder()
                                .country(Country.GERMANY)
                                .city("Berlin")
                                .postalCode("10117")
                                .street("Lepizig Str.")
                                .buildingNumber("46/47")
                                .build()
                )
                .phoneNumber("+49 444-555-666")
                .build();

        Gym fabrykaFormyGym = Gym.builder()
                .name("Fabryka Formy Katowice KTW")
                .gymAddress(
                        GymAddress.builder()
                                .country(Country.POLAND)
                                .city("Katowice")
                                .postalCode("40-203")
                                .street("al. Rozdzienskiego")
                                .buildingNumber("1")
                                .build()
                )
                .phoneNumber("+48 777-888-999")
                .build();

        gymRepository.saveAll(List.of(
                pureGym, mcFitGym, fabrykaFormyGym
        ));

        MembershipPlan pureStarter = MembershipPlan.builder()
                .name("Pure Starter")
                .type(MembershipPlanType.BASIC)
                .monthlyPrice(
                        new Money(
                                new BigDecimal("34.99"),
                                Currency.getInstance("EUR")
                        )
                )
                .durationInMonths(1)
                .maxMembers(100)
                .gym(pureGym)
                .build();

        MembershipPlan mcBasic = MembershipPlan.builder()
                .name("McBasic")
                .type(MembershipPlanType.BASIC)
                .monthlyPrice(
                        new Money(
                                new BigDecimal("22.99"),
                                Currency.getInstance("GBP")
                        )
                )
                .durationInMonths(1)
                .maxMembers(250)
                .gym(mcFitGym)
                .build();

        MembershipPlan mcPremium = MembershipPlan.builder()
                .name("McPremium")
                .type(MembershipPlanType.PREMIUM)
                .monthlyPrice(
                        new Money(
                                new BigDecimal("143.49"),
                                Currency.getInstance("EUR")
                        )
                )
                .durationInMonths(3)
                .maxMembers(60)
                .gym(mcFitGym)
                .build();

        MembershipPlan mcElite = MembershipPlan.builder()
                .name("McElite")
                .type(MembershipPlanType.PREMIUM)
                .monthlyPrice(
                        new Money(
                                new BigDecimal("999.99"),
                                Currency.getInstance("PLN")
                        )
                )
                .durationInMonths(6)
                .maxMembers(60)
                .gym(mcFitGym)
                .build();

        MembershipPlan fabrykaDuo = MembershipPlan.builder()
                .name("FabrykaDuo")
                .type(MembershipPlanType.GROUP)
                .monthlyPrice(
                        new Money(
                                new BigDecimal("79.29"),
                                Currency.getInstance("PLN")
                        )
                )
                .durationInMonths(1)
                .maxMembers(2)
                .gym(fabrykaFormyGym)
                .build();

        membershipPlanRepository.saveAll(List.of(
                pureStarter, mcBasic, mcPremium, mcElite, fabrykaDuo
        ));

        List<Member> members = List.of(
                Member.builder()
                        .firstName("Olivier")
                        .lastName("Bennett")
                        .email("oliver.bennett@example.com")
                        .membershipPlan(pureStarter)
                        .build(),
                Member.builder()
                        .firstName("Amelia")
                        .lastName("Carter")
                        .email("amelia.carter@example.com")
                        .membershipPlan(pureStarter)
                        .build(),
                Member.builder()
                        .firstName("Ethan")
                        .lastName("Price")
                        .email("ethan.price@example.com")
                        .memberStatus(MemberStatus.CANCELLED)
                        .membershipPlan(pureStarter)
                        .build(),
                Member.builder()
                        .firstName("Lukas")
                        .lastName("Neumann")
                        .email("lukas.neumann@example.com")
                        .membershipPlan(mcBasic)
                        .build(),
                Member.builder()
                        .firstName("Hannah")
                        .lastName("Fischer")
                        .email("hannah.fischer@example.com")
                        .membershipPlan(mcBasic)
                        .build(),
                Member.builder()
                        .firstName("Jonas")
                        .lastName("Vogel")
                        .email("jonas.vogel@example.com")
                        .memberStatus(MemberStatus.CANCELLED)
                        .membershipPlan(mcBasic)
                        .build(),
                Member.builder()
                        .firstName("Mila")
                        .lastName("Schneider")
                        .email("mila.schneider@example.com")
                        .membershipPlan(mcBasic)
                        .build(),
                Member.builder()
                        .firstName("Noah")
                        .lastName("Krause")
                        .email("noah.krause@example.com")
                        .membershipPlan(mcPremium)
                        .build(),
                Member.builder()
                        .firstName("Leonie")
                        .lastName("Hartmann")
                        .email("leonie.hartmann@example.com")
                        .memberStatus(MemberStatus.CANCELLED)
                        .membershipPlan(mcPremium)
                        .build(),
                 Member.builder()
                        .firstName("Kacper")
                        .lastName("Nowicki")
                        .email("kacper.nowicki@example.com")
                        .membershipPlan(mcElite)
                        .build(),
                Member.builder()
                        .firstName("Zuzanna")
                        .lastName("Wrobel")
                        .email("zuzanna.wrobel@example.com")
                        .membershipPlan(mcElite)
                        .build(),
                Member.builder()
                        .firstName("Mateusz")
                        .lastName("Jablonski")
                        .email("mateusz.jablonski@example.com")
                        .memberStatus(MemberStatus.CANCELLED)
                        .membershipPlan(mcElite)
                        .build(),
                Member.builder()
                        .firstName("Jakub")
                        .lastName("Kowalczyk")
                        .email("jakub.kowalczyk@example.com")
                        .membershipPlan(fabrykaDuo)
                        .build(),
                Member.builder()
                        .firstName("Maja")
                        .lastName("Szymanska")
                        .email("maja.szymanska@example.com")
                        .membershipPlan(fabrykaDuo)
                        .memberStatus(MemberStatus.CANCELLED)
                        .build()
        );

        memberRepository.saveAll(members);
    }
}