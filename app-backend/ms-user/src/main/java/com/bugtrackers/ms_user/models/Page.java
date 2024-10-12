package com.bugtrackers.ms_user.models;

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
@Table(name = "page")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String path;

    @ManyToOne
    @JoinColumn(name = "FK_Module")
    private Module module;

    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable;
    private LocalDateTime createdAt = LocalDateTime.now();
    
}
