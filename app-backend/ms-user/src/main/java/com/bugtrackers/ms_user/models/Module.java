package com.bugtrackers.ms_user.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Module {
    
    @Id
    private Integer id;
    
    private String name;
    
    private String direction;

    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "module")
    private List<Page> pages;
}
