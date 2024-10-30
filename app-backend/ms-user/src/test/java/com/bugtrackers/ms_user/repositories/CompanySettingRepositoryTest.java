package com.bugtrackers.ms_user.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.models.CompanySetting;

public class CompanySettingRepositoryTest {
    
    @Mock
    private CompanySettingRepository companySettingRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindByKeyName() {
        CompanySetting setting = new CompanySetting();
        setting.setKeyName("key");
        setting.setKeyValue("value");

        when(companySettingRepository.findByKeyName("key")).thenReturn(Optional.of(setting));

        Optional<CompanySetting> result = companySettingRepository.findByKeyName("key");

        assertTrue(result.isPresent());

    }

    @Test 
    void shouldNotFindByKeyName() {
        CompanySetting setting = new CompanySetting();
        setting.setKeyName("key");
        setting.setKeyValue("value");

        when(companySettingRepository.findByKeyName("keys")).thenReturn(Optional.empty());

        Optional<CompanySetting> result = companySettingRepository.findByKeyName("keys");

        assertTrue(result.isEmpty());
    }
}
