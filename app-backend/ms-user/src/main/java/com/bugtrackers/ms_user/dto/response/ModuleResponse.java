package com.bugtrackers.ms_user.dto.response;

import java.util.List;

import com.bugtrackers.ms_user.models.Module;

public record ModuleResponse(String name, List<PageResponse> pages) {

    public ModuleResponse(Module module) {
        this(module.getName(), module.getPages().stream()
                        .map(page -> new PageResponse(page.getId(), page.getName(), module.getDirection() + page.getPath(), page.getIsAvailable()))
                        .toList());
    }
    
}
