package com.bugtrackers.ms_user.repositories;

import com.bugtrackers.ms_user.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHasRoleRepository extends JpaRepository<UserRole, Integer> {
}
