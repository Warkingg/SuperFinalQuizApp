package com.example.superquizapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
public class SurveyUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int userId;

    private boolean surveyParticipated;

    private int survey_id;

    private String completion_time;

    @OneToMany(mappedBy = "surveyUser" ,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JsonManagedReference
    private Set<SurveyQuestion> surveyQuestionSet;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;
}

