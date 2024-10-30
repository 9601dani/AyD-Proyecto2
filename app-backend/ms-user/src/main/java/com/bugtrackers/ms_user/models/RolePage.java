package com.bugtrackers.ms_user.models;

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
@Table(name = "role_has_page")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "FK_Role")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "FK_Page")
    private Page page;

    @Column(columnDefinition = "TINYINT")
    private Boolean canCreate = true;

    @Column(columnDefinition = "TINYINT")
    private Boolean canEdit = true;

    @Column(columnDefinition = "TINYINT")
    private Boolean canDelete = true;
    
}
