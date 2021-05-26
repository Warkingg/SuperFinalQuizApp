package com.example.superquizapp.service;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.model.ActivityLog;
import com.example.superquizapp.model.QuestionBankTopicMap;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuizMakerService {

    List<Topic> saveTopic(Topic topic);

    void removeTopicById(Long id) throws ConstraintViolationException;

    List<Topic> findAllTopic();

    Quiz saveQuiz(Quiz quiz);

    void saveQuizUser(QuizUser quizUser);

    List<Quiz> findAllQuiz();

    Optional <Quiz> findQuizById(Long id);

    void removeQuizById(int id);
    Optional <Quiz> getQuizTopicById(Long id);

    Topic findTopicByTitle(String title);

    Topic findTopicById(Long id);

    Set<Question> fetchQuestionSet(Long quizId);

    boolean checkQuizExists(Long id);

    void updateQuestionForBlob(Long id, String s, String s1);

    List<QuizUser>  getQuizUserList(Long topicId);

    List<QuizUser> findAllQuizResult();


    List<Object[]> getQuizUserByTopic(Long topicId);

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

    List<QuestionBankTopicMap> getQuestionBankCount();

    ActivityLog getActivityByUserId(Long userId);

    List<User> fetchAssigneeList(Long id);
}
