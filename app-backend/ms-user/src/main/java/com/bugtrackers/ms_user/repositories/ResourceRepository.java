package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Integer> {
}
