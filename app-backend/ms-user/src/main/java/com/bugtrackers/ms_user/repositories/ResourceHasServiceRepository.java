package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.ResourceHasService;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ResourceHasServiceRepository extends JpaRepository<ResourceHasService, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ResourceHasService r WHERE r.service.id = :serviceId")
    void deleteByServiceId(@Param("serviceId") Integer serviceId);

    @Query("SELECT rhs FROM ResourceHasService rhs WHERE rhs.service.id = :serviceId")
    List<ResourceHasService> findAllByServiceId(@Param("serviceId") Integer serviceId);


}
