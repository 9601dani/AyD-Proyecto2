package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.ResourceHasAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceHasAttributeRepository extends JpaRepository<ResourceHasAttribute, Integer> {


}
