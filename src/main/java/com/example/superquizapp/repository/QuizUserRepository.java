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
    List<QuizUser> getQuizUserList(@Param("category_id") Long category_id);

    @Query(value = QuizQueryConstant.FAIL_COUNT_STATEWISE,nativeQuery = true)
    List<Object[]> getStateWiseFailCount();

    @Query(value=QuizQueryConstant.PASS_COUNT_STATEWISE,nativeQuery = true)
    List<Object[]> getStateWisePassCount();

    @Query(value = QuizQueryConstant.CITYWISE_FAIL_COUNT,nativeQuery = true)
    List<Object[]> getCityWiseFailCount(@Param("state") String state);

    @Query(value=QuizQueryConstant.CITYWISE_PASS_COUNT,nativeQuery = true)
    List<Object[]> getCityWisePassCount(@Param("state") String state);


    @Query(value=QuizQueryConstant.PASS_COUNT_STATEWISE_CATEGORY,nativeQuery = true)
    List<Object[]> getStateWisePassCountByCategory(@Param("categoryId") Long categoryId);

    @Query(value=QuizQueryConstant.FAIL_COUNT_STATEWISE_CATEGORY,nativeQuery = true)
    List<Object[]> getStateWiseFailCountByCategory(@Param("categoryId") Long categoryId);

    @Query(value=QuizQueryConstant.GET_QUIZ_CATEGORY,nativeQuery = true)
    List<Object[]> getQuizByCategory(@Param("categoryId") Long categoryId);

    @Query(value=QuizQueryConstant.FETCH_QUIZUSER_BY_QUIZID)
    List<QuizUser> getResultByQuizId(@Param("quizId") int quizId);

    @Query(value=QuizQueryConstant.STATE_PASS_COUNT_BY_QUIZ,nativeQuery = true)
    List<QuizUser> getPassByQuizIdStateWise(@Param("quizId") Long quizId);

    @Query(value=QuizQueryConstant.CITY_FAIL_COUNT_BY_QUIZ,nativeQuery = true)
    List<Object[]> getFailByQuizIdCityWise(@Param("quizId") int quizId,@Param("state") String state);

    @Query(value=QuizQueryConstant.CITY_PASS_COUNT_BY_QUIZ,nativeQuery = true)
    List<Object[]> getPassByQuizIdCityWise(@Param("quizId") int quizId ,@Param("state") String state);

    @Query(value=QuizQueryConstant.STATE_FAIL_COUNT_BY_QUIZ,nativeQuery = true)
    List<QuizUser> getFailByQuizIdStateWise(@Param("quizId") int quizId);

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
}
