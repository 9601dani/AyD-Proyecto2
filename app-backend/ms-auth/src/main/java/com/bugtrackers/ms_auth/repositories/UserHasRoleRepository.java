package com.bugtrackers.ms_auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.UserRole;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserRole, Integer> {
    
}
