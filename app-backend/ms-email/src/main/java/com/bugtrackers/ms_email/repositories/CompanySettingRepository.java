package com.bugtrackers.ms_email.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_email.models.CompanySetting;

@Repository
public interface CompanySettingRepository extends JpaRepository<CompanySetting, Integer> {
    
    Optional<CompanySetting> findByKeyName(String keyName);
}
