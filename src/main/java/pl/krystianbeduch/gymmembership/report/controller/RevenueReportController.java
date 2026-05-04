package pl.krystianbeduch.gymmembership.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.krystianbeduch.gymmembership.report.dto.RevenueReportResponseDto;
import pl.krystianbeduch.gymmembership.report.service.RevenueReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class RevenueReportController {

    private final RevenueReportService revenueReportService;

    @GetMapping(
            path = "/monthly-revenue",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<RevenueReportResponseDto> getMonthlyRevenueReport(
            @RequestParam(required = false) String month
    ) {
        return revenueReportService.getMonthlyRevenueReport(month);
    }
}