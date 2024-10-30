package com.bugtrackers.ms_user.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bugtrackers.ms_user.dto.request.CompanySettingRequest;
import com.bugtrackers.ms_user.dto.response.CompanySettingResponse;
import com.bugtrackers.ms_user.exceptions.CompanySettingNotFoundException;
import com.bugtrackers.ms_user.models.CompanySetting;
import com.bugtrackers.ms_user.models.SettingType;
import com.bugtrackers.ms_user.repositories.CompanySettingRepository;
import com.bugtrackers.ms_user.repositories.SettingTypeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompanySettingService {
    
    private final CompanySettingRepository companySettingRepository;
    private final SettingTypeRepository settingTypeRepository;

    public CompanySettingResponse findByKeyName(String name) {
        Optional<CompanySetting> optional = this.companySettingRepository.findByKeyName(name);
        if(optional.isEmpty()) throw new CompanySettingNotFoundException("No se encontró la configuración.");
        return new CompanySettingResponse(optional.get());
    }

    public List<CompanySettingResponse> findBySettingName(String settingName) {
        List<CompanySetting> companySettings = this.companySettingRepository.findBySettingTypeName(settingName);
        List<CompanySettingResponse>response = companySettings.stream()
            .map(CompanySettingResponse::new).toList();
        return response;
    }

    public List<CompanySettingResponse> update(List<CompanySettingRequest> companySettingRequests) {
        System.out.println(companySettingRequests);
        List<CompanySetting> companySettings = companySettingRequests.stream()
            .map(cs -> update(cs.keyName(), cs.keyValue())).toList();

        List<CompanySettingResponse> response = companySettings.stream()
            .map(CompanySettingResponse::new).toList();
        return response;
    }

    private CompanySetting update(String keyName, String keyValue) {
        Optional<CompanySetting> optional = this.companySettingRepository.findByKeyName(keyName);

        if(optional.isEmpty()) throw new CompanySettingNotFoundException("No se encontró la configuración.");

        CompanySetting companySetting = optional.get();
        companySetting.setKeyValue(keyValue);
        return this.companySettingRepository.save(companySetting);
    }

    public List<String> findAllSettingTypes() {
        List<SettingType> settingTypes = this.settingTypeRepository.findAll();
        return settingTypes.stream().map(SettingType::getName).toList();
    }
}
