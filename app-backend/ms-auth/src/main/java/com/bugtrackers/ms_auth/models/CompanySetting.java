package com.bugtrackers.ms_auth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_settings")
@Getter
@Setter
public class CompanySetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String keyName;
    private String keyValue;
    private String labelValue;
    @Column(columnDefinition = "TINYINT")
    private Boolean isRequired = false;
    @Column(columnDefinition = "TINYINT")
    private Boolean isAvailable = false;
    @ManyToOne
    @JoinColumn(name = "FK_Value_type")
    private ValueType valueType;
    @ManyToOne
    @JoinColumn(name = "FK_Setting_type")
    private SettingType settingType;
    
}
