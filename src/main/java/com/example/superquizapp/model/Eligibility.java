package com.example.superquizapp.model;

import lombok.Data;

import java.util.List;

@Data
public class Eligibility {
    private List<QuizDetails> activeQuizDetailsList;

    private List<QuizDetails> upcomingQuizDetailsList;

    private List<QuizDetails> expiredQuizDetailsList;

    private List<QuizDetails> upcomingSurveyList;

    private List<QuizDetails> activeSurveyList;
}
