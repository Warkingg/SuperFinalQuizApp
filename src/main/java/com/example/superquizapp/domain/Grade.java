package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Grade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String gradeAPlus;
    private String gradeAMinus;
    private String gradeA;

    private String gradeBPlus;
    private String gradeBMinus;
    private String gradeB;
    private String gradeCPlus;

    private String gradeCMinus;
    private String gradeC;

    private String gradeDPlus;
    private String gradeDMinus;
    private String gradeD;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;
}
