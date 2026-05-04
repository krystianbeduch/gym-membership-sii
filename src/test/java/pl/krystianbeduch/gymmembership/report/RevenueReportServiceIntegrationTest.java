package pl.krystianbeduch.gymmembership.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.gym.repository.GymRepository;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.repository.MemberRepository;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.membership.enums.MembershipPlanType;
import pl.krystianbeduch.gymmembership.membership.repository.MembershipPlanRepository;
import pl.krystianbeduch.gymmembership.report.dto.RevenueReportResponseDto;
import pl.krystianbeduch.gymmembership.report.service.RevenueReportService;
import pl.krystianbeduch.gymmembership.testdata.GymTestDataFactory;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
//@Import(RevenueReportServiceIntegrationTest.FixedClockConfig.class)
class RevenueReportServiceIntegrationTest {

    @Autowired
    private RevenueReportService revenueReportService;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        membershipPlanRepository.deleteAll();
        gymRepository.deleteAll();
    }

    @Test
    void shouldReturnMonthlyRevenueReportForGivenMonth() {
        Gym gymA = saveGym(
                GymTestDataFactory.createGym("Gym A")
        );
        Gym gymB = saveGym(
                GymTestDataFactory.createGym("Gym B")
        );

        MembershipPlan planAPln = savePlan(
                gymA,
                "Plan A PLN",
                "100.00",
                "PLN"
        );
        MembershipPlan planAEur = savePlan(
                gymA,
                "Plan A EUR",
                "50.00",
                "EUR"
        );
        MembershipPlan planBPln = savePlan(
                gymB,
                "Plan B PLN",
                "200.00",
                "PLN"
        );

        saveActiveMember(
                planAPln,
                "John",
                "One",
                "john.one@example.com"
        );
        saveActiveMember(
                planAPln,
                "John",
                "Two",
                "john.two@example.com"
        );
        saveActiveMember(
                planAEur,
                "Anna",
                "One",
                "anna.one@example.com"
        );
        saveActiveMember(
                planBPln,
                "Mike",
                "One",
                "mike.one@example.com"
        );

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("05-2026");

        assertEquals(3, result.size());
        assertEquals(new RevenueReportResponseDto(
                "Gym A", new BigDecimal("50.00"), "EUR"), result.get(0)
        );
        assertEquals(new RevenueReportResponseDto(
                "Gym A", new BigDecimal("200.00"), "PLN"), result.get(1)
        );
        assertEquals(new RevenueReportResponseDto(
                "Gym B", new BigDecimal("200.00"), "PLN"), result.get(2)
        );
    }

    @Test
    void shouldReturnMonthlyRevenueReportForCurrentMonthWhenMonthIsNull() {
        Gym gym = saveGym(
                GymTestDataFactory.createGym("Gym A")
        );
        MembershipPlan plan = savePlan(
                gym,
                "Plan A",
                "100.00",
                "PLN"
        );

        saveActiveMember(
                plan,
                "John",
                "One",
                "john.one@example.com"
        );

        /*  Because membershipStartDate uses @CreationTimestamp, Hibernate sets it automatically
            on initial persist. To simulate an older membership in this integration test,
            we first save the member and then update the date explicitly
        */
        Member oldMember = saveActiveMember(
                plan,
                "John",
                "Old",
                "john.old@example.com"
        );
        oldMember.setMembershipStartDate(LocalDate.of(2026, 3, 10));
        memberRepository.save(oldMember);

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport(null);
        List<Member> members = memberRepository.findAll();

        assertEquals(1, result.size());
        assertEquals(2, members.size());
        assertEquals(
                new RevenueReportResponseDto("Gym A", new BigDecimal("100.00"), "PLN"),
                result.getFirst()
        );
    }

    @Test
    void shouldNotIncludeCancelledMembersInReport() {
        Gym gym = saveGym(
                GymTestDataFactory.createGym("Gym A")
        );
        MembershipPlan plan = savePlan(
                gym,
                "Plan A",
                "100.00",
                "PLN"
        );

        saveActiveMember(
                plan,
                "Active",
                "User",
                "active@example.com"
        );
        saveMember(
                plan,
                "Cancelled", "User",
                "cancelled@example.com",
                MemberStatus.CANCELLED
        );

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("05-2026");

        assertEquals(1, result.size());
        assertEquals(
                new RevenueReportResponseDto("Gym A", new BigDecimal("100.00"), "PLN"),
                result.getFirst()
        );
    }

    private Gym saveGym(Gym gym) {
        return gymRepository.save(gym);
    }

    private MembershipPlan savePlan(
            Gym gym,
            String name,
            String amount,
            String currencyCode
    ) {
        MembershipPlan plan = new MembershipPlan();
        plan.setGym(gym);
        plan.setName(name);
        plan.setType(MembershipPlanType.BASIC);
        plan.setDurationInMonths(1);
        plan.setMaxMembers(10);
        plan.setMonthlyPrice(new Money(
                new BigDecimal(amount),
                Currency.getInstance(currencyCode)
        ));
        return membershipPlanRepository.save(plan);
    }

    private Member saveActiveMember(
            MembershipPlan plan,
            String firstName,
            String lastName,
            String email
    ) {
        return saveMember(plan, firstName, lastName, email, MemberStatus.ACTIVE);
    }

    private Member saveMember(
            MembershipPlan plan,
            String firstName,
            String lastName,
            String email,
            MemberStatus status
    ) {
        return memberRepository.save(
                Member.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .memberStatus(status)
                        .membershipPlan(plan)
                        .build()
        );
    }
}