package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    public Role saveRole(Role role) {
        Optional<Role> optional = this.roleRepository.findById(role.getId());

        if (optional.isPresent()) {
            throw new RuntimeException("El rol ya existe!");
        }

        return this.roleRepository.save(role);
    }



}
