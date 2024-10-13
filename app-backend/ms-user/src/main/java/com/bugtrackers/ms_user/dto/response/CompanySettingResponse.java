package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.CompanySetting;

public record CompanySettingResponse(
    Integer id,
    String keyName,
    String keyValue,
    Boolean isRequired,
    String valueType,
    String settingtype
) {
    
    public CompanySettingResponse(CompanySetting companySetting) {
        this(companySetting.getId(), 
            companySetting.getKeyName(), 
            companySetting.getKeyValue(), 
            companySetting.getIsRequired(),
            companySetting.getValueType().getName(),
            companySetting.getSettingType().getName()
        );
    }
}
