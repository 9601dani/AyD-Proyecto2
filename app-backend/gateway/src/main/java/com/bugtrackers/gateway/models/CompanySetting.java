package com.bugtrackers.gateway.models;

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
@Table(name = "company_settings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String keyName;
    private String keyValue;
    @ManyToOne
    @JoinColumn(name = "FK_Setting_type")
    private SettingType settingType;
    @ManyToOne
    @JoinColumn(name = "FK_Value_type")
    private ValueType valueType;
    
}
