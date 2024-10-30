package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.Page;

public record PageResponse(Integer id, String name, String path, Boolean isAvailable) {
    
    public PageResponse(Page page) {
        this(page.getId(), page.getName(), page.getPath(), page.getIsAvailable());
    }
}
