package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class UserQuestionMap implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long question_id;

    private Long userId;

    private Long attemptNumber;

    private int quizIndex;

    private boolean attend;

}
