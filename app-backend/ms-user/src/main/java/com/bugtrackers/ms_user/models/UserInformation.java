package com.bugtrackers.ms_user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_information")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nit;
    private String imageProfile;
    private String description;
    private String dpi;
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "FK_User")
    private User user;



    public UserInformation(String nit, String imageProfile, String description, User user, String dpi, String phoneNumber) {
        this.nit = nit;
        this.imageProfile = imageProfile;
        this.description = description;
        this.user = user;
        this.dpi = dpi;
        this.phoneNumber = phoneNumber;

    }
}
