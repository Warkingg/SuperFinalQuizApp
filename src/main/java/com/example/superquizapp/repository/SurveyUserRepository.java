package com.example.superquizapp.repository;

import com.example.superquizapp.domain.SurveyUser;
import com.example.superquizapp.utility.QuizQueryConstant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyUserRepository extends CrudRepository<SurveyUser, Long> {
    @Query(value= QuizQueryConstant.SURVEY_PARTICIPATE_QRY,nativeQuery = true)
    SurveyUser getSurveyParticipate(@Param("userId") Long userId, @Param("surveyIndex") int surveyIndex);
}
