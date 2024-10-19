package com.bugtrackers.ms_auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.EmailVerification;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {

    Optional<EmailVerification> findByTokenAndIsAvailable(String token, Boolean isAvailable);
    
}
