package com.bugtrackers.ms_user.dto.response;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record ServiceResponse(
        @NotBlank Integer id,
        @NotBlank String name,
        String description,
        @NotBlank BigDecimal price,
        String pageInformation,
        @NotBlank Integer timeAprox,
        @NotBlank Boolean isAvailable,
        List<CreateEmployeeResponse> employees,
        List<ResourceResponse> resources
) {
}
