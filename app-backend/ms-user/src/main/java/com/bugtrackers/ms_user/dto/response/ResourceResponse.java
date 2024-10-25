package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.Resource;

import java.util.List;

public record ResourceResponse(
        Integer id,
        String name,
        List<AttributeResponse> attributes
) {
        public ResourceResponse(Resource resource) {
                this(resource.getId(), resource.getName(), resource.getAttributes().stream().map(AttributeResponse::new).toList());
        }
}
