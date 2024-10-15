package com.bugtrackers.ms_user.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.CompanySetting;

@Repository
public interface CompanySettingRepository extends JpaRepository<CompanySetting, Integer> {
    
    @Query(value = """
            SELECT cs.* FROM company_settings cs
            JOIN setting_type st ON cs.FK_Setting_type = st.id
            WHERE st.name = :settingName AND cs.is_available = 1
            """, nativeQuery = true)
    List<CompanySetting> findBySettingTypeName(@Param("settingName") String settingName);
    Optional<CompanySetting> findByKeyName(String keyName);
}
