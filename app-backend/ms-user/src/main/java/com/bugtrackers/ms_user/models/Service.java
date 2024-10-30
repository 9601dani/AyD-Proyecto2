package com.bugtrackers.ms_user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private BigDecimal price;

    private String pageInformation;

    private Integer timeAprox;

    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "resource_has_service",
        joinColumns = @JoinColumn(name = "FK_Service"),
        inverseJoinColumns = @JoinColumn(name = "FK_Resource")
    )
    private List<Resource> resources = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "employee_has_service",
        joinColumns = @JoinColumn(name = "FK_Service"),
        inverseJoinColumns = @JoinColumn(name = "FK_Employee")
    )
    private List<Employee> employees = new ArrayList<>();
}
