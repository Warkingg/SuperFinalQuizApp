package com.example.superquizapp.repository;

import com.example.superquizapp.domain.SurveyQuestion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyQuestionRepository extends CrudRepository<SurveyQuestion, Long> {
}
