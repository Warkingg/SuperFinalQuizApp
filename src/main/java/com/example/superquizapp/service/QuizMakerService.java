package com.example.superquizapp.service;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.model.ActivityLog;
import com.example.superquizapp.model.QBankCategoryMap;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuizMakerService {

    List<Category> saveCategory(Category category);

    void removeCategoryById(Long id) throws ConstraintViolationException;

    List<Category> findAllCategory();

    Quiz saveQuiz(Quiz quiz);

    void saveQuizUser(QuizUser quizUser);

    List<Quiz> findAllQuiz();

    Optional <Quiz> findQuizById(Long id);

    void removeQuizById(int id);
    Optional <Quiz> getQuizCategoryById(Long id);

    Category findCategoryByTitle(String title);

    Category findCategoryById(Long id);

    Set<Question> fetchQuestionSet(Long quizId);

    boolean checkQuizExists(Long id);

    void updateQuestionForBlob(Long id, String s, String s1);

    List<QuizUser>  getQuizUserList(Long categoryId);

    List<QuizUser> findAllQuizResult();


    List<Object[]> getQuizUserByCategory(Long categoryId);

    List<QuizUser> getQuizResultByQuizId(int quizId);

    List<QuizUser> getQuizResultByUserId(int userId);


    List<Object[]> getUserDetailsById( Long userId);

    QuizUser getMaxAttempt(Long userId, int quizId);

    List<QuizUser> getAttemptNumber(Long userId, int quizId);

    List<UserQuestionMap> getUserQuestionMap(Long userId, int quizId);

    void saveAll(List<UserQuestionMap> userQuestionMapList );

    Optional<Audit> getAuditById(Long id);

    List<Audit>  getActivityLog();

    List<Activity> save(Activity activity);

    void removeActivityById(Long id);

    List<Activity> findAll();

    List<QBankCategoryMap> getQuestionBankCount();

    ActivityLog getActivityByUserId(Long userId);

    List<User> fetchAssigneeList(Long id);

    boolean checkSurveyExists(Long quizId,int surveyIndex);


    List<Object[]> stateWisePassResult();

    List<Object[]> stateWiseFailResult();

    List<Object[]> cityWisePassResult(String state);

    List<Object[]> cityWiseFailResult(String state);

    List<Object[]> getStateWisePassCountByCategory(Long categoryId);

    List<Object[]> getStateWiseFailCountByCategory(Long categoryId);

    List<Object[]> getPassByQuizIdCityWise(int quizId, String state);

    List<Object[]> getFailByQuizIdCityWise(int quizId, String state);

    List<String> getQuizPerUserRole(List<String> states, List<String>  roleids);


    SurveyUser getSurveyPartcipate(Long userId, int surveyIndex);

    SurveyUser saveSurveyUser(SurveyUser surveyUser);

    List<SurveyUser> findAllSurveyUser();

    void removeSurveyById(int id);

    Optional<User> getReviewer(Long quizId);


}
