package com.bugtrackers.ms_user.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_has_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHasService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "FK_Employee")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "FK_Service")
    private Service service;


}
