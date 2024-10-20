package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = this.roleService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("")
    public ResponseEntity<Role> saveRole(Role role) {
        Role newRole = this.roleService.saveRole(role);
        return ResponseEntity.ok(newRole);
    }
}
