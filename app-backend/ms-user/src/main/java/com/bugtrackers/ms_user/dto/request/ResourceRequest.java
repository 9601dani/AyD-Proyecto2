package com.bugtrackers.ms_user.dto.request;

import com.bugtrackers.ms_user.models.Attribute;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ResourceRequest(
        @NotBlank String name,
        @NotBlank String image,
        @NotBlank List<Attribute> attributes
) {
}
