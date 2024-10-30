package com.bugtrackers.ms_user.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
    @NotNull Integer userId,
    Integer resourceId,
    @NotBlank String startTime,
    @NotBlank String endTime,
    Integer employeeId,
    @NotNull List<Integer> servicesId
) {

}
