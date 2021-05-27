package com.example.superquizapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class SurveyQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question_type;

    private String questionFlag;

    private String survey_question;

    private String response;

    private int rating ;


    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "survey_user_id")
    private SurveyUser surveyUser;



    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;

}
