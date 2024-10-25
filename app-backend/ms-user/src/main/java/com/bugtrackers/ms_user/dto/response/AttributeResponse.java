package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.Attribute;

public record AttributeResponse(Integer id, String name, String description) {
 
    public AttributeResponse(Attribute attribute) {
        this(attribute.getId(), attribute.getName(), attribute.getDescription());
    }
}
