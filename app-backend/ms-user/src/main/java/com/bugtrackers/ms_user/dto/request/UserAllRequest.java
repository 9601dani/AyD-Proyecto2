package com.bugtrackers.ms_user.dto.request;

public record UserAllRequest(
    String nit,
    String imageProfile,
    String description
) {

    public UserAllRequest(String nit, String imageProfile, String description) {
        this.nit = nit;
        this.imageProfile = imageProfile;
        this.description = description;
    }

    public String getNit() {
        return nit;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public String getDescription() {
        return description;
    }


}
