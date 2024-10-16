package com.bugtrackers.ms_user.dto.request;
import jakarta.validation.constraints.NotBlank;

public record ImageUpdateRequest (
    String pathImg
){
    public ImageUpdateRequest(String pathImg) {
        this.pathImg = pathImg;
    }

    public String getPathImg() {
        return pathImg;
    }

}
