package com.bugtrackers.ms_user.dto.response;

import java.time.LocalDate;

public record CreateEmployeeResponse(
        Integer id,
        String firstName,
        String lastName,
        LocalDate birthOfDate
) {
}
