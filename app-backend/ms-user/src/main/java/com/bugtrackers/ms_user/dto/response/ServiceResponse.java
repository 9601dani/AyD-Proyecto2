package com.bugtrackers.ms_user.dto.response;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

import com.bugtrackers.ms_user.models.Service;

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

        public ServiceResponse(Service service) {
                this(service.getId(), service.getName(), service.getDescription(), service.getPrice()
                , service.getPageInformation(), service.getTimeAprox(), service.getIsAvailable(),
                service.getEmployees().stream().map(CreateEmployeeResponse::new).toList(), 
                service.getResources().stream().map(ResourceResponse::new).toList());
        }
}
