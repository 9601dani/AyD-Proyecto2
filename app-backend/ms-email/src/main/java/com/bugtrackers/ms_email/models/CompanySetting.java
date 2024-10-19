package com.bugtrackers.ms_email.models;

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
    private String labelValue;
    private String help;

    
}
