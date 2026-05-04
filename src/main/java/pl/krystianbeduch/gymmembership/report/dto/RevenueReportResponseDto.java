package pl.krystianbeduch.gymmembership.report.dto;

import java.math.BigDecimal;

public record RevenueReportResponseDto (
        String gymName,
        BigDecimal amount,
        String currencyCode
) { }