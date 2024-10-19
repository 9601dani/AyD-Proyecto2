package com.bugtrackers.ms_auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.User2FA;

@Repository
public interface User2FARepository extends JpaRepository<User2FA, Integer> {
    
    Optional<User2FA> findByUserIdAndSecretKeyAndIsAvailable(Integer id, String secretKey, Boolean isAvailable);
}
