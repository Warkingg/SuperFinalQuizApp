package com.example.superquizapp.repository;

import com.example.superquizapp.domain.QuizUser;
import com.example.superquizapp.utility.QuizQueryConstant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizUserRepository extends CrudRepository<QuizUser,Long> {
    @Query(value= QuizQueryConstant.FETCH_QUIZ_USER)
    List<QuizUser> getQuizUserList(@Param("topic_id") Long topic_id);

    @Query(value=QuizQueryConstant.GET_QUIZ_TOPIC,nativeQuery = true)
    List<Object[]> getQuizByTopic(@Param("topicId") Long topicId);

    @Query(value=QuizQueryConstant.FETCH_QUIZ_RESULT_BY_USER)
    List<QuizUser> fetchResultByUser(@Param("userId") int userId);

    @Query(value=QuizQueryConstant.USER_ROLE_CITY_BYID,nativeQuery = true)
    List<Object[]> getUserDetailsById(@Param("userId") Long userId);

    @Query(value=QuizQueryConstant.MAX_ATTEMPT_QRY,nativeQuery = true)
    QuizUser getMaxAttempt(@Param("userId") Long userId,@Param("quizId") int quizId);

    @Query(value=QuizQueryConstant.MAX_ATTEMPTNO_QRY,nativeQuery = true)
    List<QuizUser> getAttemptNumber(@Param("userId") Long userId,@Param("quizId") int quizId);

    @Query(value=QuizQueryConstant.GET_QUIZ_USER_ROLE,nativeQuery = true)
    List<String> getQuizPerUserRole(@Param("states")List<String> states, @Param("roleids")List<String>  roleids);

    @Query(value=QuizQueryConstant.FETCH_QUIZUSER_BY_QUIZID)
    List<QuizUser> getResultByQuizId(@Param("quizId") int quizId);

}
