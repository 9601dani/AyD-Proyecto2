package com.bugtrackers.ms_user.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String username;

    private String password;

    private String authToken;

    @Column(columnDefinition = "TINYINT")
    private Boolean isActivated = false;

    @Column(columnDefinition = "TINYINT")
    private Boolean isVerified = false;

    @Column(name = "is_2FA", columnDefinition = "TINYINT")
    private Boolean is2FA = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "user")
    private UserInformation userInformation;


    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    
}
