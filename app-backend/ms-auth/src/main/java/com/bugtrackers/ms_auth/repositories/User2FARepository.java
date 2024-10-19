package com.bugtrackers.ms_auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.User2FA;

@Repository
public interface User2FARepository extends JpaRepository<User2FA, Integer> {
    
}
