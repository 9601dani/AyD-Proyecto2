package com.bugtrackers.ms_user.models;

import java.math.BigDecimal;
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
@Table(name = "appointment_has_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentHasService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "FK_Appointment")
    private Appointment appointment;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "FK_Service")
    private Service service;
    private Integer timeAprox;
}
