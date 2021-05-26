package com.example.superquizapp.controller;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.model.ActivityLog;
import com.example.superquizapp.model.ImageModel;
import com.example.superquizapp.model.QuestionBankTopicMap;
import com.example.superquizapp.model.TopicResult;
import com.example.superquizapp.repository.ImageModelRepository;
import com.example.superquizapp.service.FileStorageService;
import com.example.superquizapp.service.QuizMakerService;
import com.example.superquizapp.service.UserService;
import com.example.superquizapp.utility.DateFormatter;
import com.example.superquizapp.utility.DeleteQuizFolder;
import com.example.superquizapp.utility.FileUploadUtil;
import com.example.superquizapp.utility.QuizConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RestController
@RequestMapping("/quizmaker")
public class QuizController {
    @Autowired
    private QuizMakerService quizMakerService;

    @Autowired
    FileStorageService storageService;

    @Autowired
    ImageModelRepository imageRepository;

    @Autowired
    private UserService userService;


    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(QuizController.class);
    private String imageName;
    // compress the image bytes before storing it in the database
    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }
    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch ( DataFormatException e) {
        }
        return outputStream.toByteArray();
    }


    @RequestMapping(value = "/add/image/{quizId}/{filename}", method = RequestMethod.POST)
    public String upload(
            @RequestParam("picture") MultipartFile multipartFile1, @PathVariable("quizId") Long quizId,
            @PathVariable("filename") String filename,
            HttpServletResponse response, HttpServletRequest request
    ) throws IOException {

        String blobExtension = "png";


        String uploadDir = "uploads/" + "quiz_"+quizId+"/";

        FileUploadUtil.saveFile(uploadDir, filename+"."+blobExtension, multipartFile1);
        Optional<Quiz> quizOptional = quizMakerService.findQuizById(quizId);
        Set<Question> updateQuestionList = new HashSet<Question>();

        return "Upload Success";
    }
    @RequestMapping(value = "/add/audio/{quizId}/{filename}", method = RequestMethod.POST)
    public String audioUpload(
            @RequestParam("audioQuestion") MultipartFile multipartFile1,@PathVariable("quizId") Long quizId,  @PathVariable("filename") String filename,

            HttpServletResponse response, HttpServletRequest request
    ) throws IOException {
        String pictureQuestion = StringUtils.cleanPath(multipartFile1.getOriginalFilename());

        String blobExtension = multipartFile1.getOriginalFilename().split("\\.")[1];
        String uploadDir = "uploads/" + "quiz_"+quizId+"/";

        FileUploadUtil.saveFile(uploadDir, filename+".mp3", multipartFile1);
        Optional<Quiz> quizOptional = quizMakerService.findQuizById(quizId);
        Set<Question> updateQuestionList = new HashSet<Question>();

        // quizMakerService.updateQuestionForBlob(quiz.getId() , filename,"http://localhost:8181/quizmaker/getAudio/"+quizId+"/"+filename+"."+blobExtension);
        return "Upload Success";

    }

    @RequestMapping(value = "/add/video/{quizId}/{filename}", method = RequestMethod.POST)
    public String videoUpload(
            @RequestParam("videoQuestion") MultipartFile videoMultipartFile,@PathVariable("quizId") Long quizId,@PathVariable("filename") String filename,


            HttpServletResponse response, HttpServletRequest request
    ) throws IOException {

        String videoQuestion = StringUtils.cleanPath(videoMultipartFile.getOriginalFilename());
        String blobExtension = videoMultipartFile.getOriginalFilename().split("\\.")[1];
        // String ideoQuestion = "question";
        String uploadDir = "uploads/" + "quiz_"+quizId+"/";

        FileUploadUtil.saveFile(uploadDir, filename+"."+blobExtension, videoMultipartFile);
        Optional<Quiz> quizOptional = quizMakerService.findQuizById(quizId);
        Set<Question> updateQuestionList = new HashSet<Question>();

        // quizMakerService.updateQuestionForBlob(quiz.getId() , filename,"http://localhost:8181/quizmaker/getVideo/"+quizId+"/"+filename);

        return "Upload  Video Question Success";
    }

    @RequestMapping(value = "/Image/{imagename}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage1(@PathVariable("imagename") String imagename) throws IOException {
        byte[] image = FileUploadUtil.extractBytes(imagename);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    @GetMapping(path = "/getVideo/{quizId}/{videoName}", produces = "video/mp4")
    public FileSystemResource getVideo(@PathVariable("videoName") String videoName, @PathVariable("quizId") Long quizId) {

        return new FileSystemResource("uploads/quiz_"+quizId+"/"+videoName+".mp4");
    }
    @GetMapping(path = "/getAudio/{quizId}/{audioName}")
    public FileSystemResource getAudio(@PathVariable("audioName") String audioName,@PathVariable("quizId") Long quizId) {

        return new FileSystemResource("uploads/quiz_"+quizId+"/"+audioName+".mp3");
    }
    @GetMapping(path = "/getImage/{quizId}/{imageName}")
    public FileSystemResource getImage(@PathVariable("imageName") String imageName,@PathVariable("quizId") Long quizId) {

        return new FileSystemResource("uploads/quiz_"+quizId+"/"+imageName+".png");
    }
    @PostMapping("/upload")
    public ResponseEntity.BodyBuilder uploadImage(@RequestParam("imageFile") MultipartFile file) throws IOException {
        System.out.println("Original Image Byte Size - " + file.getBytes().length);
        ImageModel img = new ImageModel(file.getOriginalFilename(), file.getContentType(),
                compressBytes(file.getBytes()));
        imageRepository.save(img);
        return ResponseEntity.status(HttpStatus.OK);
    }
    @RequestMapping(value = "/addActivity", method = RequestMethod.POST)
    public List<Activity> addActivity(@RequestBody Activity activity) {

        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setAudit_event("Added for "+activity.getActivity_title());
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1]);
        activity.setAudit(addAudit);

        return quizMakerService.save(activity);
    }

    @RequestMapping(value = "/activities", method = RequestMethod.GET)
    public List<Activity> getActivities() {

        return quizMakerService.findAll();
    }

    @RequestMapping(value = "/updateActivity", method = RequestMethod.POST)
    public List<Activity> updateAcitivity(@RequestBody Activity editActivity) {

        Date date = new Date();
        Optional <Audit> optionalUpdateAudit = quizMakerService.getAuditById(editActivity.getAudit().getAudit_id());
        optionalUpdateAudit.get().setAudit_event("Update for "+editActivity.getAudit().getAudit_event());
        optionalUpdateAudit.get().setDate_updated(DateFormatter.formatDate(date));
        optionalUpdateAudit.get().setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));

        editActivity.setAudit(editActivity.getAudit());
        return quizMakerService.save(editActivity);
    }

    @RequestMapping(value = "/deleteActivity/{id}", method = RequestMethod.GET)
    public ResponseEntity removeActivity( @PathVariable("id") Long id) {
        quizMakerService.removeActivityById(id);
        return new ResponseEntity("Delete Success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/addTopic", method = RequestMethod.POST)
    public List<Topic> addTopic(@RequestBody Topic topic) {
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit = topic.getAudit();
        addAudit.setAudit_event("Added Topic "+ topic.getTopic_title());
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        topic.setAudit(addAudit);

        return quizMakerService.saveTopic(topic);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public List<Topic> getTopics() {

        return quizMakerService.findAllTopic().stream().filter(x-> x.getTopic_title()!="").collect(Collectors.toList());
    }

    @RequestMapping(value = "/updateTopic", method = RequestMethod.POST)
    public List<Topic> updateTopic(@RequestBody Topic updateTopic) {

        Date date = new Date();
        Audit updateAudit = new Audit();
        updateAudit = updateTopic.getAudit();//quizMakerService.getAuditById(updateCategory.getAudit().getAudit_id());
        //updateAudit.setAudit_event("Update Category "+updateCategory.getAudit().getAudit_event());
        updateAudit.setDate_created(quizMakerService.findTopicById(updateTopic.getTopic_id()).getAudit().getDate_created());
        updateAudit.setTime_created(quizMakerService.findTopicById(updateTopic.getTopic_id()).getAudit().getTime_created());
        updateAudit.setDate_updated(DateFormatter.formatDate(date));
        updateAudit.setUserId(1l);
        updateAudit.setAudit_event("Update for "+updateTopic.getTopic_title());
        updateAudit.setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        //  updateCategory.setcategory_desc(quizMakerService.findCategoryById(updateCategory.getcategory_id()).getcategory_desc());
        updateTopic.setAudit(updateAudit);
        return quizMakerService.saveTopic(updateTopic);
    }

    @RequestMapping(value = "/deleteTopic/{id}", method = RequestMethod.GET)
    public ResponseEntity removeTopic( @PathVariable("id") Long id) {

        quizMakerService.removeTopicById(id);
        return new ResponseEntity("Delete Success!", HttpStatus.OK);
    }

    @RequestMapping(value = "/activityLog", method = RequestMethod.GET)
    public List<Audit> getActivityLog() {

        return  quizMakerService.getActivityLog();

    }

    @RequestMapping(path = "/download", produces = "application/octet-stream; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Resource> download(String param) throws IOException {

        File file = new File("uploads/quiz_118"+"/"+"252_Question_Audio.mp3");
        HttpHeaders header = new HttpHeaders();
        //header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.jpg");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        InputStreamResource resource = new InputStreamResource(new FileInputStream("uploads/quiz_118"+"/"+"252_Question_Audio.mp3"));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @RequestMapping(value = "/addQuiz", method = RequestMethod.POST)
    @Transactional

    public Quiz addQuiz(@RequestBody Quiz quiz) {
        Audit addAudit =new Audit();
        Date date = new Date();
        if(quizMakerService.checkQuizExists(quiz.getId())){
            Optional<Quiz> optionalAuditQuiz = quizMakerService.findQuizById(quiz.getId());
            quizMakerService.removeQuizById(quiz.getQuizIndex());
            addAudit.setAudit_event("Update Quiz : "+quiz.getQuiz_title());
            addAudit.setDate_created(optionalAuditQuiz.get().getAudit().getDate_created());
            addAudit.setTime_created(optionalAuditQuiz.get().getAudit().getTime_created());
            addAudit.setDate_updated(DateFormatter.formatDate(date));
            addAudit.setUserId(quiz.getCreatorId());
            addAudit.setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
            quiz.setId(quiz.getId());
            quiz.setQuizIndex(quiz.getQuizIndex());
            // DeleteQuizFolder.deleteQuizFolder(quiz.getId());


        } else {
            addAudit.setAudit_event("New Quiz Created Event "+quiz.getQuiz_title());
            addAudit.setDate_created(DateFormatter.formatDate(date));
            addAudit.setUserId(quiz.getCreatorId());
            addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
            List<Activity> activityList = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Quiz")).collect(Collectors.toList());

            List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList.get(0).getActivity_id()).collect(Collectors.toList());


            quiz.setQuizIndex(quizList.size()+1);
        }

        List<Quiz> quizList = new ArrayList<Quiz>();
        quizList.add(quiz);
        Topic topic = new Topic();
        topic.setTopic_id(quiz.getTopic().getTopic_id());
        Set<Question> questionSet = new HashSet<Question>();
        for (Question questionObj: quiz.getQuestionList()){
            Question question = new Question();
            question.setQuestionSeq(questionObj.getQuestionSeq());
            //question.setResponseList(questionObj.getResponseList());
            Set<Answer> answerSet = new HashSet<Answer>();
            for(Answer answer : questionObj.getAnswerList()){
                Answer answerObj = new Answer();
                answerObj.setResponse(answer.getResponse());
                answerObj.setCorrectAnswerFlag(answer.isCorrectAnswerFlag());
                answerObj.setQuestion(question);
                answerObj.setResponseSeq(answer.getResponseSeq());
                answerObj.setBlobUrl(answer.getBlobUrl());
                answerSet.add(answerObj);
            }
            question.setAnswerList(answerSet);
            quiz.setTopic(topic);
            quiz.setDate_schedule(quiz.getScheduleDateTime().split("T")[0]);
            quiz.setTime_schedule(quiz.getScheduleDateTime().split("T")[1]);
            quiz.setScheduleDateTime(quiz.getScheduleDateTime());
            question.setQuiz(quiz);
            questionSet.add(question);

        }
        quiz.setQuestionList(questionSet);
        quiz.setRandomQuestions(quiz.getRandomQuestions());
        quiz.setQuizSetting(quiz.getQuizSetting());

        LOG.info(" After setting question Quiz :"+ quiz);


        Quiz saveQuiz =  quizMakerService.saveQuiz(quiz);
        return saveQuiz;
        //return new ResponseEntity("Quiz Added Successfully", HttpStatus.OK);
    }
    @RequestMapping(value = "/getQuizAssignee/{quizId}", method = RequestMethod.GET)
    public List<User> getQuizAssignee(@PathVariable("quizId") Long quizId)  {

        List<Activity> activityList1 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Quiz")).collect(Collectors.toList());
        List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList1.get(0).getActivity_id() && x.getQuizIndex() == quizId).collect(Collectors.toList());
        LOG.info("Quiz***** {}",quizList.size());

        List<User> userList = userService.getUserList();
        List<User> assigneeUsers = new ArrayList<User>();
        List<String> roleLIst = Arrays.asList(quizList.get(0).getAssigneeClassList()!=null?quizList.get(0).getAssigneeRoleList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
        for(User user : userList ) {
            if(quizList.size()>0 && quizList.get(0).getAssigneeClassList().contains(user.getClassroom())){
                if(roleLIst.contains(userService.getUserRole(user.getId()).stream().collect(Collectors.toList()).get(0).getRole().getName())
                ){
                    assigneeUsers.add(user);

                }

            }

        }

        return assigneeUsers;
    }
    @RequestMapping(value = "/getQuizAttemptLeft/{userId}/{quizIndex}", method = RequestMethod.GET)
    public int getQuizAttempt(@PathVariable("userId") int userId , @PathVariable("quizIndex") int quizIndex) {


        int  maxAttemptLeft =quizMakerService.getMaxAttempt(Long.valueOf(userId),quizIndex)!=null?quizMakerService.getMaxAttempt(Long.valueOf(userId),quizIndex).getMax_attempt_left():-1;
        /** for(QuizUser qu:quizUserList){

         if(qu.getQuiz_id() == quizIndex ){
         maxAttemptLeft = qu.getMax_attempt_left();
         }

         } **/

        return maxAttemptLeft;


    }
    private int getDaysDifference(String currentDate, String date_schedule) throws ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date currentdate = myFormat.parse(currentDate);
        Date quizdate = myFormat.parse(date_schedule);
        long difference = currentdate.getTime() - quizdate.getTime();
        float daysBetween = (difference / (1000*60*60*24));

        return (int)daysBetween;
    }

    @RequestMapping(value = "/getQuestionBankCount", method = RequestMethod.GET)
    public  List<QuestionBankTopicMap>  getQuestionBankCount() {

        List<QuestionBankTopicMap> questionBankMap = new ArrayList<QuestionBankTopicMap>();

        questionBankMap = quizMakerService.getQuestionBankCount();

        return questionBankMap;

    }

    @RequestMapping(value = "/getQuizEligibility/{userId}/{quizId}", method = RequestMethod.GET)
    public  QuizUser  getMaxAtemptLeft(@PathVariable("userId") Long userId,@PathVariable("quizId") int quizId) {



        return quizMakerService.getMaxAttempt(userId,quizId);

    }

    @RequestMapping(value = "/getResultByUserId/{userId}", method = RequestMethod.GET)
    public  List<QuizUser>  getResultByUserId(@PathVariable("userId") int userId) {

        return quizMakerService.getQuizResultByUserId(userId);

    }


    @RequestMapping(value = "/getResultByTopic/{topicId}", method = RequestMethod.GET)
    public TopicResult getResultByTopicResult(@PathVariable("topicId") Long topicId) {

        TopicResult topicResult = new TopicResult();
        List<QuizUser>  quizUserList = quizMakerService.getQuizUserList(topicId);

        // Count the pass
        long passCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("PASS"))
                .count();
        // Count the pass
        long failCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("FAIL"))
                .count();

        topicResult.setPassCount(passCount);
        topicResult.setFailCount(failCount);
        topicResult.setTopicId(topicId);

        return topicResult;

    }

    @RequestMapping(value = "/getAllResult", method = RequestMethod.GET)
    public  TopicResult  getAllResult() {

        TopicResult topicResult = new TopicResult();
        List<QuizUser>  quizUserList = quizMakerService.findAllQuizResult();

        // Count the pass
        long passCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("PASS"))
                .count();
        // Count the pass
        long failCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("FAIL"))
                .count();

        topicResult.setPassCount(passCount);
        topicResult.setFailCount(failCount);


        return topicResult;

    }

    @RequestMapping(value = "/getActivity/{userid}", method = RequestMethod.GET)
    public ActivityLog activityLog(@PathVariable("userid") Long userId) {
        return quizMakerService.getActivityByUserId(userId);
    }

    @RequestMapping(value = "/getQuizUserByTopic/{topicId}", method = RequestMethod.GET)
    public List<String> getQuizUserByTopic(@PathVariable("topicId") Long topicId) {

        List<String> quizList = new ArrayList<String>();

        for (Object[] quizObject : quizMakerService.getQuizUserByTopic(topicId)){
            String quizTitle = quizObject[0].toString();
            String quizId = quizObject[1].toString();
            String quizobj = quizTitle+"_"+quizId;
            quizList.add(quizobj);
        }
        return quizList;
    }
    @RequestMapping(value = "/deleteQuiz/{id}", method = RequestMethod.GET)
    public ResponseEntity removeQuiz( @PathVariable("id") int id) {
        quizMakerService.removeQuizById(id);
        DeleteQuizFolder.deleteQuizFolder(id);
        return new ResponseEntity("Delete Success!", HttpStatus.OK);
    }
    @RequestMapping(value = "/getAssigneeList/{quiz_id}", method = RequestMethod.GET)
    public List<User> fetchReviewerList( @PathVariable("quiz_id") Long id) {
        return quizMakerService.fetchAssigneeList(id);
        //return new ResponseEntity("Delete Success!", HttpStatus.OK);
    }
    @RequestMapping(value = "/addQuizIntro", method = RequestMethod.POST)
    @Transactional
    public List<Quiz> addQuizIntro(@RequestBody Quiz quiz) {
        // public  Quiz addQuizIntro(@RequestBody Quiz quiz) {
        List<Quiz> quizList= null;
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setAudit_event(quiz.getActivity().getActivity_id().equals(3)?"New Survey Created Event":"New Quiz Created Event");
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        addAudit.setUserId(quiz.getCreatorId());
        quiz.setAudit(addAudit);
        quiz.setStatus(QuizConstants.IN_DESIGN);

        Quiz saveQuiz =quizMakerService.saveQuiz(quiz);

        if(quiz.getActivity().getActivity_id().equals(2)) {
            List<Activity>  activityList1 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Quiz")).collect(Collectors.toList());
            quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList1.get(0).getActivity_id()).collect(Collectors.toList());
        }else{
            List<Activity> activityList2 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Poll")).collect(Collectors.toList());
            quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList2.get(0).getActivity_id()).collect(Collectors.toList());
        }


        return quizList;
        //return new ResponseEntity("Quiz Intro Added Successfully", HttpStatus.OK);
    }
    @RequestMapping(value = "/submitQuiz", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> submitQuiz(@RequestBody QuizUser quizUser) {


        List<Question> questionList = new ArrayList<Question>();
        Question questionPer = new Question();
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setAudit_event("New Quiz Submitted Event :"+quizUser.getQuiz_id());
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        addAudit.setUserId(Long.valueOf(quizUser.getUserId()));
        quizUser.setCompletion_date(DateFormatter.formatDate(date));
        // quizUser.setCompletion_time(DateFormatter.formatDateAndTime(date).split(" ")[1]);

        quizUser.setAudit(addAudit);
        quizMakerService.saveQuizUser(quizUser);
        if(quizUser.getResult().equalsIgnoreCase("FAIL") && quizUser.getQuestionList().size() >0 ){
            UserQuestionMap userQuestionMap = null;
            List<UserQuestionMap> userQuestionMapList = new ArrayList<UserQuestionMap>();

            for(Question question:quizUser.getQuestionList()){
                userQuestionMap = new UserQuestionMap();
                userQuestionMap.setQuestion_id(question.getId());
                userQuestionMap.setQuizIndex(quizUser.getQuiz_id());
                userQuestionMap.setAttend(true);
                userQuestionMap.setUserId(Long.valueOf(quizUser.getUserId()));
                userQuestionMap.setAttemptNumber(getAttemptNumber(quizUser.getUserId(),quizUser.getQuiz_id()));
                userQuestionMapList.add(userQuestionMap);
            }
            quizMakerService.saveAll(userQuestionMapList);

        }
        return new ResponseEntity("Quiz Submitted Successfully", HttpStatus.OK);
    }

    private Long getAttemptNumber(int userId, int quiz_id) {

        int  attemptNumber =quizMakerService.getAttemptNumber(Long.valueOf(userId),quiz_id)!=null?quizMakerService.getAttemptNumber(Long.valueOf(userId),quiz_id).size():1;

        return Long.valueOf(attemptNumber);

    }
    @RequestMapping(value = "/getQuizTOpic/{id}", method = RequestMethod.GET)
    @Transactional

    public ResponseEntity fetchQuizTopic(@PathVariable("id") Long quizId) {

        Optional<Quiz> quiz = quizMakerService.getQuizTopicById(quizId);
        LOG.info("Quiz Topic"+ quiz.get().getTopic().getTopic_title());

        return new ResponseEntity("Quiz topic is "+quiz.get().getTopic().getTopic_title(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUserQuestionMap/{userId}/{quiz_id}", method = RequestMethod.GET)
    public List<UserQuestionMap> getUserQuestionMap( @PathVariable("quiz_id") int quizId, @PathVariable("userId") Long userId) {
        return quizMakerService.getUserQuestionMap(userId,quizId);

    }

    @RequestMapping(value = "/quizList", method = RequestMethod.GET)
    public List<Quiz> getQuizList() {

        List<Activity> activityList = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Quiz")).collect(Collectors.toList());

        List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList.get(0).getActivity_id()).collect(Collectors.toList());

        return quizList;
    }
    @RequestMapping(value = "/getQuizByIndex/{quizIndex}", method = RequestMethod.GET)
    public Quiz getQuizByIndex(@PathVariable("quizIndex") int quizIndex ) {


        Quiz quiz = new Quiz();
        List<Quiz> quizList = quizMakerService.findAllQuiz().stream()
                .filter(x -> x.getQuizIndex()== quizIndex).collect(Collectors.toList());

        if(quizList.size()>0) {
            return quizList.get(0);
        }else {
            quiz.setIntroduction_message("Quiz Not Avaialble");
            return  quiz;
        }

    }
    @RequestMapping(value = "/getQuestionSet/{id}", method = RequestMethod.GET)
    @Transactional

    public Set<Question> fetQuestionSetByQuiz(@PathVariable("id") Long quizId) {

        return quizMakerService.fetchQuestionSet(quizId);
    }


}

