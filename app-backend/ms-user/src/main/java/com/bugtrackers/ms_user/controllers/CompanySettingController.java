package com.bugtrackers.ms_user.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_user.dto.request.CompanySettingRequest;
import com.bugtrackers.ms_user.dto.response.CompanySettingResponse;
import com.bugtrackers.ms_user.services.CompanySettingService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/company-settings")
@AllArgsConstructor
public class CompanySettingController {

    private final CompanySettingService companySettingService;

    @GetMapping("/{name}")
    public ResponseEntity<CompanySettingResponse> findByKeyName(@PathVariable String name) {
        CompanySettingResponse response = this.companySettingService.findByKeyName(name);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/setting-type/{name}")
    public ResponseEntity<List<CompanySettingResponse>> findBySettingName(@PathVariable String name) {
        List<CompanySettingResponse> response = this.companySettingService.findBySettingName(name);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    @Transactional
    public ResponseEntity<List<CompanySettingResponse>> update(@RequestBody @Valid List<CompanySettingRequest> companySettingRequest) {
        List<CompanySettingResponse> response = this.companySettingService.update(companySettingRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/setting-types")
    public ResponseEntity<List<String>> findAllSettingTypes() {
        List<String> names = this.companySettingService.findAllSettingTypes();
        return ResponseEntity.ok(names);
    }

    

}
