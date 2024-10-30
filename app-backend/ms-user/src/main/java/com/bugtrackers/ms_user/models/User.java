package com.bugtrackers.ms_user.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_has_role",
        joinColumns = @JoinColumn(name = "FK_User"),
        inverseJoinColumns = @JoinColumn(name = "FK_Role")
    )
    private List<Role> roles = new ArrayList<>();



    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(int i, String mail, String user1, String password1, String authToken1, boolean b, boolean b1, boolean b2, LocalDateTime now) {
    }
}
