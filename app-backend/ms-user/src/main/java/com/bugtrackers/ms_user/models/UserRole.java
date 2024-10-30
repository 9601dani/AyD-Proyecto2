package com.bugtrackers.ms_user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_has_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "FK_User")
    private User user;
    @ManyToOne
    @JoinColumn(name = "FK_Role")
    private Role role;
}
