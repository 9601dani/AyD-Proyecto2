package com.bugtrackers.ms_auth.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_verification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String token;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiredAt = LocalDateTime.now().plusHours(1);
    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable = true;
    
}
