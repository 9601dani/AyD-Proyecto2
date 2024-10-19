package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
}
