package com.bugtrackers.ms_auth.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bugtrackers.ms_auth.models.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long>{

    User findByEmail(String email);
    User findByUsername(String username);

}
