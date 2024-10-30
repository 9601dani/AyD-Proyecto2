package com.bugtrackers.ms_user.dto.response;

import java.time.LocalDateTime;

public record BillReportResponse(
        String name, String nit, String address, Double totalAmount, Double advancement, LocalDateTime billDate
) {
}
