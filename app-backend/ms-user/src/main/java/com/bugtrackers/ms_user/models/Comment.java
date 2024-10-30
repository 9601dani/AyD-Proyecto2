package com.bugtrackers.ms_user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "FK_User")
    private User user;

    private String comment;

    @Min(1)
    @Max(5)
    @Column(columnDefinition = "TINYINT")
    private Integer value;

    private LocalDateTime createdAt = LocalDateTime.now();
}
