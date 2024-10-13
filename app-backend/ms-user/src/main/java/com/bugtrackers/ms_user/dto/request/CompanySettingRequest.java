package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanySettingRequest(
    @NotNull Integer id,
    @NotBlank String keyName,
    @NotBlank String keyValue,
    @NotNull Boolean isRequired,
    @NotBlank String valueType,
    @NotBlank String settingType
) {
    
}
