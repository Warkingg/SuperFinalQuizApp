package com.example.superquizapp.utility;

public interface QuizQueryConstant {
    public static final String FETCH_QUIZ_RESULT_BY_USER = " FROM QuizUser qu where qu.userId=:userId";

    public static final String FETCH_QUIZ_USER ="FROM QuizUser where  quiz_topic_id=:topic_id";

    public static final String FETCH_QUIZUSER_BY_QUIZID ="FROM QuizUser where  quiz_id=:quizId";

    public static final String FAIL_COUNT_STATEWISE ="select count(*) as fail_count,usr.state from  quiz_user qu  inner join User usr on usr.id=qu.user_id where qu.result='FAIL'   group by usr.state";

    public static final String PASS_COUNT_STATEWISE = "select count(*) as fail_count,usr.state from  quiz_user qu  inner join User usr on usr.id=qu.user_id where qu.result='PASS'   group by usr.state";

    public static final String PASS_COUNT_STATEWISE_TOPIC = "select count(*) as pass_count,usr.state from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='PASS' and qu.quiz_topic_id=:topicId  group by usr.state";

    public static final String FAIL_COUNT_STATEWISE_TOPIC = "select count(*) as pass_count,usr.state from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='FAIL' and qu.quiz_topic_id=:topicId  group by usr.state";

    public static final String STATE_PASS_COUNT_BY_QUIZ = "select count(*) as pass_count,usr.state from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='PASS' and qu.quiz_id=:quizId  group by usr.state";

    public static final String STATE_FAIL_COUNT_BY_QUIZ = "select count(*) as pass_count,usr.state from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='FAIL' and qu.quiz_id=:quizId   group by usr.state";

    public static final String CITY_PASS_COUNT_BY_QUIZ = "select count(*) as pass_count,usr.city from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='PASS' and qu.quiz_id=:quizId and usr.state=:state group by usr.city";

    public static final String CITY_FAIL_COUNT_BY_QUIZ = "select count(*) as pass_count,usr.city from  quiz_user qu inner join User usr on usr.id=qu.user_id where qu.result='FAIL' and qu.quiz_id=:quizId and usr.state=:state  group by usr.city";

    public static final String CITYWISE_FAIL_COUNT =  " select count(*) as pass_count,usr.city from  quiz_user qu  inner join User usr on usr.id=qu.user_id where qu.result='FAIL' and usr.state=:state group by usr.city";

    public static final String CITYWISE_PASS_COUNT =  " select count(*) as pass_count,usr.city from  quiz_user qu  inner join User usr on usr.id=qu.user_id where qu.result='PASS' and usr.state=:state group by usr.city";

    public static final String GET_QUIZ_TOPIC ="select distinct(q.quiz_title) , qu.quiz_id FROM quizmaker.quiz_user qu inner join quiz q on q.quiz_index=qu.quiz_id where qu.quiz_topic_id=:topicId";

    public static final String GET_QUIZ_USER_ROLE ="select usr.id from user usr inner join user_role ur on usr.id = ur.user_id where usr.city IN(:states) and ur.role_id IN(:roleids) ";

    public static  final String USER_ROLE_CITY_BYID ="SELECT usr.city,rl.name FROM quizmaker.user usr inner join user_role ur on ur.user_id=usr.id inner join role rl on rl.role_id=ur.role_id where usr.id=:userId";
    public static final String MAX_ATTEMPT_QRY ="select * from quizmaker.quiz_user where user_id=:userId and quiz_id=:quizId and id=(select max(id) from quizmaker.quiz_user where user_id=:userId and quiz_id=:quizId)";

    public static final String MAX_ATTEMPTNO_QRY ="select * from quizmaker.quiz_user where user_id=:userId and quiz_id=:quizId";

    public static final String GET_USER_QUESTION_MAP_QRY="SELECT * FROM quizmaker.user_question_map where quiz_index=:quizIndex and user_id=:userId and attempt_number=(select max(attempt_number) FROM quizmaker.user_question_map) ";

    public static final String SURVEY_PARTICIPATE_QRY = "select * from quizmaker.survey_user where user_id=:userId and survey_id=:surveyIndex ";

    public static  final String DELETE_EXISTING_ROLE_BY_ID =" DELETE FROM user_role where user_id=:userId";

    public static final String GET_ONLINE_USERS ="SELECT au.audit_event FROM quizmaker.audit au inner join  quizmaker.user usr on usr.id=au.user_id \n" +
            "where au.audit_id = ( select max(audit_id) FROM quizmaker.audit where user_id=:userId) and usr.show_status=true ;";

    public static final String SURVEY_RES_STAT ="select count(*) , rating  from quizmaker.survey_question where question_type='Rating' \n" +
            "and survey_user_id IN ( select id from quizmaker.survey_user where survey_id=:surveyId) group by rating";
}
