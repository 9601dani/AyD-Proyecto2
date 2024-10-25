package com.bugtrackers.ms_user.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attribute")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "resource_has_attribute",
        joinColumns = @JoinColumn(name = "FK_Attribute"),
        inverseJoinColumns = @JoinColumn(name = "FK_Resource")
    )
    private List<Resource> resources = new ArrayList<>();
}
