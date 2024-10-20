package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.Attribute;

import java.util.List;

public record ResourceResponse(
        Integer id,
        String name,
        List<Attribute> attributes
) {
}
