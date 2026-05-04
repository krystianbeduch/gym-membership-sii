package pl.krystianbeduch.gymmembership.report;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.krystianbeduch.gymmembership.gym.entity.Gym;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.service.MemberService;
import pl.krystianbeduch.gymmembership.membership.entity.MembershipPlan;
import pl.krystianbeduch.gymmembership.membership.entity.Money;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.report.dto.RevenueReportResponseDto;
import pl.krystianbeduch.gymmembership.report.exception.InvalidReportMonthFormatException;
import pl.krystianbeduch.gymmembership.report.service.RevenueReportService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueReportServiceUnitTest {

    @Mock
    private MemberService memberService;

    private Clock clock;
    private RevenueReportService revenueReportService;

    @BeforeEach
    void setUp() {
        // Fixed clock to make "current month" logic deterministic in tests
        clock = Clock.fixed(
                Instant.parse("2026-05-05T12:00:00.00Z"),
                ZoneId.of("UTC")
        );
        revenueReportService = new RevenueReportService(memberService, clock);
    }

    @Test
    void getMonthlyRevenueReport_shouldReturnReportForGivenMonthGroupedByGymAndCurrency() {
        Member member1 = createActiveMember(
                "Gym A", "100.00", "PLN", 1, LocalDate.of(2026, 5, 10)
        );
        Member member2 = createActiveMember(
                "Gym A", "100.00", "PLN", 1, LocalDate.of(2026, 5, 11)
        );
        Member member3 = createActiveMember(
                "Gym A", "50.00", "EUR", 1, LocalDate.of(2026, 5, 12)
        );
        Member member4 = createActiveMember(
                "Gym B", "200.00", "PLN", 1, LocalDate.of(2026, 5, 13)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member1, member2, member3, member4));

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("05-2026");

        assertEquals(3, result.size());
        assertEquals(new RevenueReportResponseDto("Gym A", new BigDecimal("50.00"), "EUR"), result.get(0));
        assertEquals(new RevenueReportResponseDto("Gym A", new BigDecimal("200.00"), "PLN"), result.get(1));
        assertEquals(new RevenueReportResponseDto("Gym B", new BigDecimal("200.00"), "PLN"), result.get(2));
    }

    @Test
    void getMonthlyRevenueReport_shouldReturnReportForCurrentMonthWhenMonthIsNull() {
        Member member1 = createActiveMember(
                "Gym A", "100.00", "PLN", 1, LocalDate.of(2026, 5, 10)
        );
        Member member2 = createActiveMember(
                "Gym B", "200.00", "EUR", 1, LocalDate.of(2026, 8, 10)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member1, member2));

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport(null);

        assertEquals(1, result.size());
        assertEquals(new RevenueReportResponseDto("Gym A", new BigDecimal("100.00"), "PLN"), result.getFirst());
    }

    @Test
    void getMonthlyRevenueReport_shouldReturnReportForCurrentMonthWhenMonthIsBlank() {
        Member member = createActiveMember(
                "Gym A", "120.00", "PLN", 1, LocalDate.of(2026, 5, 5)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member));

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("  ");

        assertEquals(1, result.size());
        assertEquals(new RevenueReportResponseDto("Gym A", new BigDecimal("120.00"), "PLN"), result.getFirst());
    }

    @Test
    void getMonthlyRevenueReport_shouldIncludeMemberInFollowingMonthsWhenPlanDurationIsLongerThanOneMonth() {
        Member member = createActiveMember(
                "Gym A", "300.00", "PLN", 2, LocalDate.of(2026, 5, 10)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member));

        List<RevenueReportResponseDto> juneResult =
                revenueReportService.getMonthlyRevenueReport("06-2026");

        List<RevenueReportResponseDto> julyResult =
                revenueReportService.getMonthlyRevenueReport("07-2026");

        List<RevenueReportResponseDto> augustResult =
                revenueReportService.getMonthlyRevenueReport("08-2026");

        assertEquals(1, juneResult.size());
        assertEquals(1, julyResult.size());
        assertTrue(augustResult.isEmpty());
    }

    @Test
    void getMonthlyRevenueReport_shouldReturnEmptyListWhenNoActiveMembersMatchMonth() {
        Member member = createActiveMember(
                "Gym A", "100.00", "PLN", 1, LocalDate.of(2026, 1, 10)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member));

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("05-2026");

        assertTrue(result.isEmpty());
    }

    @Test
    void getMonthlyRevenueReport_shouldThrowExceptionWhenMonthHasInvalidFormat() {
        assertThrows(
                InvalidReportMonthFormatException.class,
                () -> revenueReportService.getMonthlyRevenueReport("2026-05")
        );
    }

    @Test
    void getMonthlyRevenueReport_shouldReturnSortedByGymNameAndCurrencyCode() {
        Member member1 = createActiveMember(
                "Gym B", "100.00", "PLN", 1, LocalDate.of(2026, 5, 10)
        );
        Member member2 = createActiveMember(
                "Gym A", "50.00", "PLN", 1, LocalDate.of(2026, 5, 10)
        );
        Member member3 = createActiveMember(
                "Gym A", "25.00", "EUR", 1, LocalDate.of(2026, 5, 10)
        );

        when(memberService.getAllMembersEntityByStatus(MemberStatus.ACTIVE))
                .thenReturn(List.of(member1, member2, member3));

        List<RevenueReportResponseDto> result =
                revenueReportService.getMonthlyRevenueReport("05-2026");

        assertEquals("Gym A", result.get(0).gymName());
        assertEquals("EUR", result.get(0).currencyCode());

        assertEquals("Gym A", result.get(1).gymName());
        assertEquals("PLN", result.get(1).currencyCode());

        assertEquals("Gym B", result.get(2).gymName());
        assertEquals("PLN", result.get(2).currencyCode());
    }

    private Member createActiveMember(
            String gymName,
            String amount,
            String currencyCode,
            int durationInMonths,
            LocalDate startDate
    ) {
        Gym gym = Gym.builder().name(gymName).build();
        MembershipPlan plan = MembershipPlan.builder()
                .gym(gym)
                .durationInMonths(durationInMonths)
                .monthlyPrice(new Money(
                        new BigDecimal(amount), Currency.getInstance(currencyCode)
                ))
                .build();

        return Member.builder()
                .membershipStartDate(startDate)
                .membershipPlan(plan)
                .build();
    }
}
