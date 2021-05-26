package com.example.superquizapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question_type;

    private String question;

    private int question_mark;

    private String blobUrl;

    private Long questionSeq;

    @JsonIgnore
    private AnswerType answerType;

    @OneToOne(cascade = CascadeType.ALL)
    private AnswerType getAnswerType(){return answerType;}

    public void setAnswerType(AnswerType answerType){this.answerType = answerType;}

    @OneToMany(mappedBy = "question" ,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JsonManagedReference
    private Set<Answer> answerList;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @JsonBackReference("a")
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.DETACH)
    @JoinColumn(name = "quiz_user_id")
    private QuizUser quizUser;

}
