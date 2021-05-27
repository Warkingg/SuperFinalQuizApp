package com.example.superquizapp.service.impl;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.model.ActivityLog;
import com.example.superquizapp.model.QBankCategoryMap;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SurveyUserRepository surveyUserRepository;

    @Autowired
    private QuizUserRepository quizUserRepository;

    @Autowired
    private UserQuestionMapRepository userQuestionMapRepository;


    @Autowired
    private UserRepository userRepository;


    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(QuizMakerServiceImpl.class);

    @Override
    public List<Category> saveCategory(Category category) {
        categoryRepository.save(category);

        List<Category> categoryList= (List<Category>) categoryRepository.findAll();


        return categoryList;
    }

    @Override
    public void removeCategoryById(Long id) throws ConstraintViolationException {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> findAllCategory() {
        List<Category> categoryList= (List<Category>) categoryRepository.findAll();
        categoryList = categoryList.stream().filter(x-> x.getCateogry_title()!="").collect(Collectors.toList());

        return categoryList;
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
    }

    @Override
    public Optional<Quiz> getQuizCategoryById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Category findCategoryByTitle(String title) {
        List<Category> categoryList = ( List<Category>)categoryRepository.findAll();
        Category categories = new Category();
        for(Category category:categoryList){
            if(category.getCateogry_title().equals(title)){
                categories = category;
            }

        }

        return categories;
    }

    @Override
    public Category findCategoryById(Long id) {
        List<Category> categoryList = ( List<Category>) categoryRepository.findAll();
        Category categories = new Category();
        for(Category category:categoryList){
            if(category.getCategory_id().equals(id)){
                categories = category;
                break;
            }
        }
        return categories;
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
    }

    @Override
    public List<QuizUser> getQuizUserList(Long categoryId) {
        return quizUserRepository.getQuizUserList(categoryId);
    }

    @Override
    public List<QuizUser> findAllQuizResult() {
        return (List<QuizUser>)quizUserRepository.findAll();
    }

    @Override
    public List<Object[]> getQuizUserByCategory(Long categoryId) {
        return quizUserRepository.getQuizByCategory(categoryId);
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
    public boolean checkSurveyExists(Long quizId, int surveyIndex) {
        boolean flag = false;
        Optional<Quiz> optionalSurvey = quizRepository.findById(quizId);
        if(optionalSurvey.get().getSurveyIndex()!=0 && optionalSurvey.get().getSurveyIndex()==surveyIndex){
            flag =  true;
        }
        return flag;
    }

    @Override
    public List<Object[]> stateWisePassResult() {
        return quizUserRepository.getStateWisePassCount();
    }

    @Override
    public List<Object[]> stateWiseFailResult() {
        return quizUserRepository.getStateWiseFailCount();
    }

    @Override
    public List<Object[]> cityWisePassResult(String state) {
        return quizUserRepository.getCityWisePassCount(state);
    }

    @Override
    public List<Object[]> cityWiseFailResult(String state) {
        return quizUserRepository.getCityWiseFailCount(state);
    }

    @Override
    public List<Object[]> getStateWisePassCountByCategory(Long categoryId) {
        return quizUserRepository.getStateWisePassCountByCategory(categoryId);
    }

    @Override
    public List<Object[]> getStateWiseFailCountByCategory(Long categoryId) {
        return quizUserRepository.getStateWiseFailCountByCategory(categoryId);
    }

    @Override
    public List<Object[]> getPassByQuizIdCityWise(int quizId, String state) {
        return quizUserRepository.getPassByQuizIdCityWise(quizId,state);
    }

    @Override
    public List<Object[]> getFailByQuizIdCityWise(int quizId, String state) {
        return quizUserRepository.getFailByQuizIdCityWise(quizId,state);
    }

    @Override
    public List<String> getQuizPerUserRole(List<String> states, List<String> roleids) {
        return quizUserRepository.getQuizPerUserRole(states,roleids);
    }

    @Override
    public SurveyUser getSurveyPartcipate(Long userId, int surveyIndex) {
        return surveyUserRepository.getSurveyParticipate(userId,surveyIndex);
    }

    @Override
    public SurveyUser saveSurveyUser(SurveyUser surveyUser) {
        return surveyUserRepository.save(surveyUser);
    }

    @Override
    public List<SurveyUser> findAllSurveyUser() {
        return (List<SurveyUser>)surveyUserRepository.findAll();
    }

    @Override
    @Transactional
    public void removeSurveyById(int id) {
        Long quizId=  quizRepository.getSurveyIdByIndex(id);
        quizRepository.deleteById(quizId);
    }

    @Override
    public Optional<User> getReviewer(Long quizId) {
        String reviewer = quizRepository.fetchReviewer(quizId);
        return userRepository.findById(Long.valueOf(reviewer));
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
    public List<QBankCategoryMap> getQuestionBankCount() {
        List<Category> categoryIdList = quizRepository.getDistinctCategories();
        QBankCategoryMap qBankCategoryMap =null;
        List<QBankCategoryMap> questionBankCountList = new ArrayList<QBankCategoryMap>();
        for(Category category : categoryIdList){
            qBankCategoryMap =  new QBankCategoryMap();
            qBankCategoryMap.setCategory(category);
            qBankCategoryMap.setQuestionBankcount(quizRepository.getBankPerCategory(category.getCategory_id()));
            questionBankCountList.add(qBankCategoryMap);
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
