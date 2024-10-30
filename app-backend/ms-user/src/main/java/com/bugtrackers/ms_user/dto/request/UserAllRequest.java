package com.bugtrackers.ms_user.dto.request;


public record UserAllRequest(
    String nit,
    String imageProfile,
    String description,
    String dpi,
    String phoneNumber

) {

    public UserAllRequest(String nit, String imageProfile, String description, String dpi, String phoneNumber) {
        this.nit = nit;
        this.imageProfile = imageProfile;
        this.description = description;
        this.dpi = dpi;
        this.phoneNumber = phoneNumber;

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

    public String getDpi() {
        return dpi;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


}
