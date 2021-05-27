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
    private ResponseType responseType;

    @OneToOne(cascade = CascadeType.ALL)
    private ResponseType getResponseType(){return responseType;}

    public void setResponseType(ResponseType responseType){this.responseType = responseType;}

    @OneToMany(mappedBy = "question" ,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JsonManagedReference
    private Set<Response> responseList;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @JsonBackReference("a")
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.DETACH)
    @JoinColumn(name = "quiz_user_id")
    private QuizUser quizUser;

    @OneToOne(cascade = CascadeType.ALL)
    private FeedBack feedBack;

    public String getQuestion_title() {
        return question;
    }

    public void setQuestion_title(String question_title) {
        this.question = question_title;
    }
}
