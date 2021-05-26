package com.example.superquizapp.model;

import lombok.Data;

@Data
public class QuizDetails {
    private String quizTitle;

    private String quizTopic;

    private int quizIndex;

    private int max_attempt_left;

    private String status;

    private String level;

    private String date;
}
