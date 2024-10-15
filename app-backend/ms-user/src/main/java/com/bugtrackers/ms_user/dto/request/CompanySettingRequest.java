package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CompanySettingRequest(
    @NotBlank String keyName,
    @NotBlank String keyValue,
    @NotBlank String valueType
) {
    
}
