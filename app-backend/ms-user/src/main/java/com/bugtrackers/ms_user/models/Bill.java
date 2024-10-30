package com.bugtrackers.ms_user.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bill")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nit;
    private String name;
    private String address;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt = LocalDateTime.now();
    private BigDecimal advancement = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "FK_Appointment")
    private Appointment appointment;
    
}
