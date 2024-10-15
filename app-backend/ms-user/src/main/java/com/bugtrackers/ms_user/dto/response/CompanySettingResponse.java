package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.CompanySetting;

public record CompanySettingResponse(
    Integer id,
    String keyName,
    String keyValue,
    String labelValue,
    Boolean isRequired,
    String valueType,
    String settingtype,
    String help
) {
    
    public CompanySettingResponse(CompanySetting companySetting) {
        this(companySetting.getId(), 
            companySetting.getKeyName(), 
            companySetting.getKeyValue(), 
            companySetting.getLabelValue(),
            companySetting.getIsRequired(),
            companySetting.getValueType().getName(),
            companySetting.getSettingType().getName(),
            companySetting.getHelp()
        );
    }
}
