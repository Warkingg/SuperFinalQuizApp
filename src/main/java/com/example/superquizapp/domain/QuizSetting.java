package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class QuizSetting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String checkBoxStyle;

    private String fontFace;

    private String radioStyle;

    private boolean defaultCheck;
    private boolean materialCheck;
    private boolean filledCheck;

    private boolean normalRadio;
    private boolean animatedRadio;
    private boolean squareRadio;

    private String dropdownStyle;

    private String backgroundColor;

    private String fontColor;

    private String totalTime;

    private boolean reviewQuestionFlag;
}
