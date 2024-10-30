package com.bugtrackers.ms_user.repositories;

import java.util.Optional;

import com.bugtrackers.ms_user.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
}
