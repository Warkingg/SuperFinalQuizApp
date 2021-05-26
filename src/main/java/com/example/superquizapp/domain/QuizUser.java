package com.example.superquizapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
public class QuizUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int userId;

    private int quiz_id;

    private int max_attempt_left;

    private Long quiz_topic_id;

    private int score;

    private String grade;

    private String result;

    private  int percentage;

    private int max_attempt;

    private String completion_time;

    private String completion_date;

    private int quizDesign;

    private int questionDifficulty;

    private int timeConstraint;

    @OneToMany(mappedBy = "quizUser" ,fetch = FetchType.EAGER,cascade=CascadeType.DETACH)
    @JsonManagedReference("a")
    private List<Question> questionList;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;
}
