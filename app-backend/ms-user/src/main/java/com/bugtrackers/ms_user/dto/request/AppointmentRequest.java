package com.bugtrackers.ms_user.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
    @NotNull Integer userId,
    Integer resourceId,
    @NotNull String startTime,
    @NotNull String endTime,
    Integer employeeId,
    @NotNull List<Integer> servicesId
) {

}
