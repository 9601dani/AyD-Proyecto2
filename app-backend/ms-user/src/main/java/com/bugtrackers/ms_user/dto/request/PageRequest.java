package com.bugtrackers.ms_user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PageRequest(
    @NotNull Integer id, 
    @NotBlank String name, 
    @NotBlank String path, 
    Boolean isAvailable) {
}
