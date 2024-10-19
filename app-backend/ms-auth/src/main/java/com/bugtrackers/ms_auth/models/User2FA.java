package com.bugtrackers.ms_auth.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_2fa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User2FA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String secretKey;
    @ManyToOne
    @JoinColumn(name = "FK_User")
    private User user;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiredAt = LocalDateTime.now().plusHours(1);
    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable = true;

}
