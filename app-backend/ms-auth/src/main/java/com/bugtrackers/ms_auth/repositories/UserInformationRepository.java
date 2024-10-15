package com.bugtrackers.ms_auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.UserInformation;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    
}
