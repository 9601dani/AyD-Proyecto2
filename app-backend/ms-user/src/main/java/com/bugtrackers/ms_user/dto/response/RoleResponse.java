package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.Role;

public record RoleResponse(Integer id, String name, String description) {
 
    public RoleResponse(Role role) {
        this(role.getId(), role.getName(), role.getDescription());
    }
}
