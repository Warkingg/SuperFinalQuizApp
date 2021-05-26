package com.example.superquizapp.service.impl;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.model.ActivityLog;
import com.example.superquizapp.model.QuestionBankTopicMap;
import com.example.superquizapp.repository.*;
import com.example.superquizapp.service.QuizMakerService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class QuizMakerServiceImpl implements QuizMakerService {

    @Autowired
    private QuizMakerService quizMakerService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizUserRepository quizUserRepository;

    @Autowired
    private UserQuestionMapRepository userQuestionMapRepository;

    @Autowired
    private UserRepository userRepository;

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(QuizMakerServiceImpl.class);

    @Override
    public List<Topic> saveTopic(Topic topic) {
        topicRepository.save(topic);

        List<Topic> topicList= (List<Topic>) topicRepository.findAll();


        return topicList;
    }

    @Override
    public void removeTopicById(Long id) throws ConstraintViolationException {
        topicRepository.deleteById(id);
    }

    @Override
    public List<Topic> findAllTopic() {
        List<Topic> topicList= (List<Topic>) topicRepository.findAll();
        topicList = topicList.stream().filter(x-> x.getTopic_title()!="").collect(Collectors.toList());

        return topicList;
    }

    @Override
    @Transactional
    public Quiz saveQuiz(Quiz quiz) {
        return  quizRepository.save(quiz);
    }

    @Override
    public void saveQuizUser(QuizUser quizUser) {
        quizUserRepository.save(quizUser);
    }

    @Override
    public List<Quiz> findAllQuiz() {
        return  (List<Quiz>)quizRepository.findAll();
    }

    @Override
    public Optional<Quiz> findQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public void removeQuizById(int id) {
        Long quizId = quizRepository.getQuizIdByIndex(id);
        quizRepository.deleteById(quizId);
    }

    @Override
    public Optional<Quiz> getQuizTopicById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Topic findTopicByTitle(String title) {
        List<Topic> topicList = ( List<Topic>)topicRepository.findAll();
        Topic topics = new Topic();
        for(Topic topic:topicList){
            if(topic.getTopic_title().equals(title)){
                topics = topic;
            }

        }

        return topics;
    }

    @Override
    public Topic findTopicById(Long id) {
        List<Topic> topicList = ( List<Topic>)topicRepository.findAll();
        Topic topics = new Topic();
        for(Topic topic:topicList){
            if(topic.getTopic_id().equals(id)){
                topics = topic;
                break;
            }
        }
        return topics;
    }

    @Override
    public Set<Question> fetchQuestionSet(Long quizId) {
        Optional<Quiz> quizOptional = quizRepository.findById(quizId);

        return quizOptional.get().getQuestionList();
    }

    @Override
    public boolean checkQuizExists(Long id) {
        boolean flag = false;
        if(quizRepository.findById(id)!=null){
            flag =  true;
        }

        return flag;
    }

    @Override
    public void updateQuestionForBlob(Long id, String s, String s1) {
        Long questionId = Long.parseLong(s.split("_")[0]);
        quizRepository.updateBlobUrl(id,questionId,s1);
    }

    @Override
    public List<QuizUser> getQuizUserList(Long topicId) {
        return quizUserRepository.getQuizUserList(topicId);
    }

    @Override
    public List<QuizUser> findAllQuizResult() {
        return (List<QuizUser>)quizUserRepository.findAll();
    }

    @Override
    public List<Object[]> getQuizUserByTopic(Long topicId) {
        return quizUserRepository.getQuizByTopic(topicId);
    }

    @Override
    public List<QuizUser> getQuizResultByQuizId(int quizId) {
        return quizUserRepository.getResultByQuizId( quizId);
    }

    @Override
    public List<QuizUser> getQuizResultByUserId(int userId) {
        return quizUserRepository.fetchResultByUser(userId);
    }

    @Override
    public List<Object[]> getUserDetailsById(Long userId) {
        return quizUserRepository.getUserDetailsById(userId);
    }

    @Override
    public QuizUser getMaxAttempt(Long userId, int quizId) {
        return quizUserRepository.getMaxAttempt(userId,quizId);
    }

    @Override
    public List<QuizUser> getAttemptNumber(Long userId, int quizId) {
        return quizUserRepository.getAttemptNumber(userId,quizId);
    }

    @Override
    public List<UserQuestionMap> getUserQuestionMap(Long userId, int quizId) {
        return (List<UserQuestionMap>)userQuestionMapRepository.getUserQuestionMap(userId,quizId);
    }

    @Override
    public void saveAll(List<UserQuestionMap> userQuestionMapList) {
        userQuestionMapRepository.saveAll(userQuestionMapList);
    }

    @Override
    public Optional<Audit> getAuditById(Long id) {
        return auditRepository.findById(id);
    }

    @Override
    public List<Audit> getActivityLog() {
        return (List<Audit>)auditRepository.findAll();
    }

    @Override
    public List<Activity> save(Activity activity) {
        activityRepository.save(activity);

        List<Activity> activityList= (List<Activity>) activityRepository.findAll();


        return activityList;
    }

    @Override
    public void removeActivityById(Long id) {
        activityRepository.deleteById(id);
    }

    @Override
    public List<Activity> findAll() {
        List<Activity> activityList = (List<Activity>)  activityRepository.findAll();
        return activityList ;
    }

    @Override
    public List<QuestionBankTopicMap> getQuestionBankCount() {
        List<Topic> topicIdList = quizRepository.getDistinctTopics();
        QuestionBankTopicMap questionBankTopicMap =null;
        List<QuestionBankTopicMap> questionBankCountList = new ArrayList<QuestionBankTopicMap>();
        for(Topic topic : topicIdList){
            questionBankTopicMap =  new QuestionBankTopicMap();
            questionBankTopicMap.setTopic(topic);
            questionBankTopicMap.setQuestionBankcount(quizRepository.getBankPerTopic(topic.getTopic_id()));
            questionBankCountList.add(questionBankTopicMap);
        }
        return questionBankCountList;
    }

    @Override
    public ActivityLog getActivityByUserId(Long userId) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setAuditing(auditRepository.findByUserId(userId));
        return activityLog;
    }

    @Override
    public List<User> fetchAssigneeList(Long id) {
        String assignee = quizRepository.fetchAssigneeListById(id);
        List<String> assigneeList = Stream.of(assignee.split(",", -1))
                .collect(Collectors.toList());
        LOG.info("User Assignee List "+assigneeList.toString());
        List<User> userList = new ArrayList<User>();
        for(String userId: assigneeList ){
            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));
            userList.add(optionalUser.get());
        }

        return userList;
    }
}
