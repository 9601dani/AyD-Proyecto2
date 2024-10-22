package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

    public Optional<Service> findByName(String name);
}
