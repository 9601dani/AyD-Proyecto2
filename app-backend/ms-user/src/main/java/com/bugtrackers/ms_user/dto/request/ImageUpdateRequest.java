package com.bugtrackers.ms_user.dto.request;

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
