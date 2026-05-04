package pl.krystianbeduch.gymmembership.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.krystianbeduch.gymmembership.member.entity.Member;
import pl.krystianbeduch.gymmembership.member.service.MemberService;
import pl.krystianbeduch.gymmembership.membership.enums.MemberStatus;
import pl.krystianbeduch.gymmembership.report.dto.RevenueReportKey;
import pl.krystianbeduch.gymmembership.report.dto.RevenueReportResponseDto;
import pl.krystianbeduch.gymmembership.report.exception.InvalidReportMonthFormatException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueReportService {

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MM-yyyy");
    private final MemberService memberService;
    private final Clock clock;

    public List<RevenueReportResponseDto> getMonthlyRevenueReport(
            String month
    ) {
        YearMonth targetMonth = parseMonth(month);
        log.info("Fetching monthly revenue report for month {}", targetMonth);

        List<Member> activeMembers = memberService.getAllMembersEntityByStatus(
                MemberStatus.ACTIVE
        );

        Map<RevenueReportKey, BigDecimal> groupedRevenue = new HashMap<>();
        for (Member member : activeMembers) {
            if (!isActiveInMonth(member, targetMonth)) {
                continue;
            }

            RevenueReportKey key = new RevenueReportKey(
                    member.getMembershipPlan().getGym().getName(),
                    member.getMembershipPlan().getMonthlyPrice().getCurrency().getCurrencyCode()
            );
            BigDecimal amount = member.getMembershipPlan().getMonthlyPrice().getAmount();
            groupedRevenue.merge(key, amount, BigDecimal::add);
        }
        return groupedRevenue.entrySet().stream()
                .map(entry -> new RevenueReportResponseDto(
                        entry.getKey().gymName(),
                        entry.getValue(),
                        entry.getKey().currencyCode()
                ))
                .sorted(Comparator
                        .comparing(RevenueReportResponseDto::gymName)
                        .thenComparing(RevenueReportResponseDto::currencyCode)
                ).toList();
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now(clock);
        }
        try {
            return YearMonth.parse(month, MONTH_FORMATTER);
        }
        catch (DateTimeParseException ex) {
            throw new InvalidReportMonthFormatException(
                     "Invalid month format '" + month + "'. Expected MM-yyyy, e.g. 05-2026"
            );
        }
    }

    private boolean isActiveInMonth(Member member, YearMonth targetMonth) {
        LocalDate startDate = member.getMembershipStartDate();
        int durationInMonths = member.getMembershipPlan().getDurationInMonths();
        LocalDate endDate = startDate.plusMonths(durationInMonths);

        LocalDate firstDayOfTargetMonth = targetMonth.atDay(1);
        LocalDate lastDayOfTargetMonth = targetMonth.atEndOfMonth();

        return !startDate.isAfter(lastDayOfTargetMonth)
                && !endDate.isBefore(firstDayOfTargetMonth);
    }
}