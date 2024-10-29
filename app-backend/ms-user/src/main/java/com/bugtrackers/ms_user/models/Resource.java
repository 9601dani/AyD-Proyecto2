package com.bugtrackers.ms_user.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String image;

    @ManyToMany(mappedBy = "resources")
    private List<Service> services = new ArrayList<>();
    @ManyToMany(mappedBy = "resources")
    private List<Attribute> attributes = new ArrayList<>();
}
