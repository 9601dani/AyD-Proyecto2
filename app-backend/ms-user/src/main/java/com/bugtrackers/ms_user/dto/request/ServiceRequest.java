package com.bugtrackers.ms_user.dto.request;

import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Resource;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record ServiceRequest(
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
