package com.bugtrackers.ms_user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_has_attribute")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceHasAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "FK_Resource")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "FK_Attribute")
    private Attribute attribute;
}
