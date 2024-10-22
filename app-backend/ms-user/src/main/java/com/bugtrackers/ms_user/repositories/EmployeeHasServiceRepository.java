package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.EmployeeHasService;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeHasServiceRepository extends JpaRepository<EmployeeHasService, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM EmployeeHasService e WHERE e.service.id = :serviceId")
    void deleteByServiceId(@Param("serviceId") Integer serviceId);

    @Query("SELECT ehs FROM EmployeeHasService ehs WHERE ehs.service.id = :serviceId")
    List<EmployeeHasService> findAllByServiceId(@Param("serviceId") Integer serviceId);
}
