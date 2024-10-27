package com.bugtrackers.ms_user.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "FK_User")
    private User user;
    @ManyToOne
    @JoinColumn(name = "FK_Resource")
    private Resource resource;
    private BigDecimal total;
    private String state = "PENDING";
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ManyToOne
    @JoinColumn(name = "FK_Employee")
    private Employee employee;
    @OneToMany(mappedBy = "appointment")
    private List<AppointmentHasService> appointmentHasServices = new ArrayList<>();
    
}
