package com.bugtrackers.ms_user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_user.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {    
    
}
