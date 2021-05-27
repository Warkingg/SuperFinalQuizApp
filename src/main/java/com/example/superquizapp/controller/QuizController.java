package com.example.superquizapp.controller;

import com.example.superquizapp.domain.*;
import com.example.superquizapp.domain.security.Role;
import com.example.superquizapp.domain.security.UserRole;
import com.example.superquizapp.model.*;
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
import java.math.BigInteger;
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
    @PostMapping("/addActivity")
    public List<Activity> addActivity(@RequestBody Activity activity) {

        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setAudit_event("Added for "+activity.getActivity_title());
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1]);
        activity.setAudit(addAudit);

        return quizMakerService.save(activity);
    }

    @GetMapping("/activities")
    public List<Activity> getActivities() {

        return quizMakerService.findAll();
    }

    @PostMapping("/updateActivity")
    public List<Activity> updateAcitivity(@RequestBody Activity editActivity) {

        Date date = new Date();
        Optional <Audit> optionalUpdateAudit = quizMakerService.getAuditById(editActivity.getAudit().getAudit_id());
        optionalUpdateAudit.get().setAudit_event("Update for "+editActivity.getAudit().getAudit_event());
        optionalUpdateAudit.get().setDate_updated(DateFormatter.formatDate(date));
        optionalUpdateAudit.get().setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));

        editActivity.setAudit(editActivity.getAudit());
        return quizMakerService.save(editActivity);
    }

    @GetMapping("/deleteActivity/{id}")
    public ResponseEntity removeActivity( @PathVariable("id") Long id) {
        quizMakerService.removeActivityById(id);
        return new ResponseEntity("Delete Success!", HttpStatus.OK);
    }

    @PostMapping("/addCategory")
    public List<Category> addCategory(@RequestBody Category category) {
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit = category.getAudit();
        addAudit.setAudit_event("Added Category "+ category.getCateogry_title());
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        category.setAudit(addAudit);

        return quizMakerService.saveCategory(category);
    }

    @GetMapping("/topics")
    public List<Category> getTopics() {

        return quizMakerService.findAllCategory().stream().filter(x-> x.getCateogry_title()!="").collect(Collectors.toList());
    }

    @PostMapping("/updateCategory")
    public List<Category> updateCategory(@RequestBody Category updateCategory) {

        Date date = new Date();
        Audit updateAudit = new Audit();
        updateAudit = updateCategory.getAudit();//quizMakerService.getAuditById(updateCategory.getAudit().getAudit_id());
        //updateAudit.setAudit_event("Update Category "+updateCategory.getAudit().getAudit_event());
        updateAudit.setDate_created(quizMakerService.findCategoryById(updateCategory.getCategory_id()).getAudit().getDate_created());
        updateAudit.setTime_created(quizMakerService.findCategoryById(updateCategory.getCategory_id()).getAudit().getTime_created());
        updateAudit.setDate_updated(DateFormatter.formatDate(date));
        updateAudit.setUserId(1l);
        updateAudit.setAudit_event("Update for "+ updateCategory.getCateogry_title());
        updateAudit.setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        //  updateCategory.setcategory_desc(quizMakerService.findCategoryById(updateCategory.getcategory_id()).getcategory_desc());
        updateCategory.setAudit(updateAudit);
        return quizMakerService.saveCategory(updateCategory);
    }

    @GetMapping(value = "/deleteCategory/{id}")
    public ResponseEntity removeCategory( @PathVariable("id") Long id) {

        quizMakerService.removeCategoryById(id);
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
        Audit feedbackAudit =new Audit();
        Date date = new Date();
        if(quizMakerService.checkQuizExists(quiz.getId())){
            Optional<Quiz> auditquiz = quizMakerService.findQuizById(quiz.getId());
            quizMakerService.removeQuizById(quiz.getQuizIndex());
            addAudit.setAudit_event("Update Quiz : "+quiz.getQuiz_title());
            addAudit.setDate_created(auditquiz.get().getAudit().getDate_created());
            addAudit.setTime_created(auditquiz.get().getAudit().getTime_created());
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

        LOG.info("Quiz Object "+quiz);
        List<String> assigneeUserList = null;//Arrays.asList(elements);


        quiz.setAudit(addAudit);
        if(quiz.getAssigneeUserList()!=null){
            String[] assigneeList = quiz.getAssigneeUserList().split(",");
            assigneeUserList = Arrays.asList(assigneeList);
            quiz.setAssigneeUserList(quiz.getAssigneeUserList());
        }

        User reviewer = new User();

        User invigilator = new User();
        if(quiz.getInvigilator()!=0 && quiz.getInvigilator()!=null) {
            invigilator.setId(Long.valueOf(quiz.getInvigilator()));
        }
        if(quiz.getReviewer()!=0 && quiz.getReviewer()!=null) {
            reviewer.setId(Long.valueOf(quiz.getReviewer()));
        }

        //invigilator.setUsername(quiz.getInvigilator().getUsername());
        Role role = new Role();
        role.setRoleId(1);
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        Set<UserRole> roleList =new HashSet<UserRole>();
        roleList.add(userRole);
        reviewer.setUserRoles(roleList);

        Set<User> userList = new HashSet<User>();
        userList.add(reviewer);

        List<Quiz> quizList = new ArrayList<Quiz>();
        quizList.add(quiz);
        Category category = new Category();
        category.setCategory_id(quiz.getCategory().getCategory_id());
        Set<Question> questionSet = new HashSet<Question>();
        for (Question questionObj: quiz.getQuestionList()){
            Question question = new Question();
            FeedBack feedBack = questionObj.getFeedBack();
            feedbackAudit.setAudit_event("FeedBack created for quiz :"+quiz.getQuizIndex());
            feedbackAudit.setDate_created(DateFormatter.formatDate(date));
            feedbackAudit.setUserId(quiz.getCreatorId());
            feedbackAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
            feedBack.setAudit(feedbackAudit);
            question.setQuestion_type(questionObj.getQuestion_type());
            question.setQuestion_title(questionObj.getQuestion_title());
            question.setQuestion_mark(questionObj.getQuestion_mark());
            question.setBlobUrl(questionObj.getBlobUrl());
            question.setFeedBack(feedBack);

            question.setQuestionSeq(questionObj.getQuestionSeq());
            //question.setResponseList(questionObj.getResponseList());
            Set<Response> responseSet = new HashSet<Response>();
            for(Response response : questionObj.getResponseList()){
                Response responseObj = new Response();
                responseObj.setResponse(response.getResponse());
                responseObj.setCorrectAnswerFlag(response.isCorrectAnswerFlag());
                responseObj.setQuestion(question);
                responseObj.setResponseSeq(response.getResponseSeq());
                responseObj.setBlobUrl(response.getBlobUrl());
                responseSet.add(responseObj);
            }
            question.setResponseList(responseSet);
            quiz.setCategory(category);
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

    @RequestMapping(value = "/postSurvey", method = RequestMethod.POST)
    @Transactional

    public SurveyUser postSurvey(@RequestBody SurveyUser surveyUser) {
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setUserId(Long.valueOf(surveyUser.getUserId()));
        addAudit.setAudit_event("New Poll Submit Event :"+surveyUser.getSurvey_id());
        Set<SurveyQuestion> surveyQuestions = new HashSet<SurveyQuestion>();
        for (SurveyQuestion surveyQuestionObj : surveyUser.getSurveyQuestionSet()) {

            SurveyQuestion surveyQuestion = new SurveyQuestion();
            surveyQuestion.setQuestion_type(surveyQuestionObj.getQuestion_type());
            surveyQuestion.setSurvey_question(surveyQuestionObj.getSurvey_question());
            surveyQuestion.setResponse(surveyQuestionObj.getResponse());
            surveyQuestion.setRating(surveyQuestionObj.getRating());
            surveyQuestion.setSurveyUser(surveyUser);
            surveyQuestions.add(surveyQuestion);

        }
        surveyUser.setSurveyQuestionSet(surveyQuestions);
        surveyUser.setAudit(addAudit);
        SurveyUser savedSurveyUser = quizMakerService.saveSurveyUser(surveyUser);
        return savedSurveyUser;
    }
    @RequestMapping(value = "/addSurvey", method = RequestMethod.POST)
    @Transactional

    public Quiz addSurvey(@RequestBody Quiz quiz) {
        Audit addAudit =new Audit();
        Audit feedbackAudit =new Audit();
        Date date = new Date();
        if(quiz.getId()!=null) {
            if(quizMakerService.checkSurveyExists(quiz.getId(),quiz.getSurveyIndex())) {
                Optional<Quiz> auditquiz = quizMakerService.findQuizById(quiz.getId());
                quizMakerService.removeSurveyById(quiz.getSurveyIndex());
                addAudit.setAudit_event("Update Survey " + quiz.getQuiz_title());
                addAudit.setDate_created(auditquiz.get().getAudit().getDate_created() != null ? auditquiz.get().getAudit().getDate_created() : null);
                addAudit.setTime_created(auditquiz.get().getAudit().getTime_created() != null ? auditquiz.get().getAudit().getTime_created() : null);
                addAudit.setDate_updated(DateFormatter.formatDate(date));
                addAudit.setUserId(quiz.getCreatorId());
                addAudit.setTime_updated(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
                quiz.setId(quiz.getId());
                quiz.setSurveyIndex(quiz.getSurveyIndex());
                // DeleteQuizFolder.deleteQuizFolder(quiz.getId());

            }
        } else {
            addAudit.setAudit_event("New Survey Created Event "+quiz.getQuiz_title());
            addAudit.setDate_created(DateFormatter.formatDate(date));
            addAudit.setUserId(quiz.getCreatorId());
            addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1].concat(DateFormatter.formatDateAndTime(date).split(" ")[2]));
            List<Activity> activityList = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Poll")).collect(Collectors.toList());

            List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList.get(0).getActivity_id()).collect(Collectors.toList());


            quiz.setSurveyIndex(quizList.size()+1);
        }

        LOG.info("Quiz Object "+quiz);


        List<Quiz> quizList = new ArrayList<Quiz>();
        quizList.add(quiz);

        Set<Question> questionSet = new HashSet<Question>();
        for (Question questionObj: quiz.getQuestionList()){
            Question question = new Question();

            question.setQuestion_type(questionObj.getQuestion_type());

            question.setQuestion_title(questionObj.getQuestion_title());
            question.setQuestionSeq(questionObj.getQuestionSeq());
            //question.setResponseList(questionObj.getResponseList());
            Set<Response> responseSet = new HashSet<Response>();
            for(Response response : questionObj.getResponseList()){
                Response responseObj = new Response();
                responseObj.setSurveyRatingOption(response.isSurveyRatingOption());
                responseObj.setSurveyTextOption(response.isSurveyTextOption());
                responseObj.setQuestion(question);
                responseObj.setResponse(response.getResponse()!=null?response.getResponse():null);
                responseObj.setRating(response.getRating()!=0?response.getRating():0);
                responseSet.add(responseObj);
            }
            question.setResponseList(responseSet);
            quiz.setDate_schedule(quiz.getDate_schedule());
            question.setQuiz(quiz);
            questionSet.add(question);

        }
        quiz.setQuestionList(questionSet);
        quiz.setActivity(quiz.getActivity());
        quiz.setAudit(addAudit);

        LOG.info(" Survey :"+ quiz);

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
        List<String> roleLIst = Arrays.asList(quizList.get(0).getAssigneeCityList()!=null?quizList.get(0).getAssigneeRoleList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
        for(User user : userList ) {
            if(quizList.size()>0 && quizList.get(0).getAssigneeCityList().contains(user.getCity())){
                if(roleLIst.contains(userService.getUserRole(user.getId()).stream().collect(Collectors.toList()).get(0).getRole().getName())
                ){
                    assigneeUsers.add(user);

                }

            }

        }

        return assigneeUsers;
    }

    @RequestMapping(value = "/getSurveyAssignee/{surveyId}", method = RequestMethod.GET)
    public List<User> getSurveyAssignee(@PathVariable("surveyId") Long surveyId)  {

        List<Activity> activityList1 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Poll")).collect(Collectors.toList());
        List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList1.get(0).getActivity_id() && x.getSurveyIndex() == surveyId).collect(Collectors.toList());
        LOG.info("Survey ***** {}",quizList.size());

        List<User> userList = userService.getUserList();
        List<User> assigneeUsers = new ArrayList<User>();
        List<String> roleLIst = Arrays.asList(quizList.get(0).getAssigneeCityList()!=null?quizList.get(0).getAssigneeRoleList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
        for(User user : userList ) {
            if(quizList.size()>0 && quizList.get(0).getAssigneeCityList().contains(user.getCity())){
                if(roleLIst.contains(userService.getUserRole(user.getId()).stream().collect(Collectors.toList()).get(0).getRole().getName())
                ){
                    assigneeUsers.add(user);

                }

            }

        }

        return assigneeUsers;
    }



    @RequestMapping(value = "/getQuizEligibility/{userId}", method = RequestMethod.GET)

    public Eligibility getQuizEligibility(@PathVariable("userId") Long userId) throws ParseException {


        String city =null;String role=null;
        Eligibility eligibility = new Eligibility();
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate= myFormat.format(date);
        // List<Quiz>  quizList = quizMakerService.findAllQuiz();

        List<Activity> activityList1 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Quiz")).collect(Collectors.toList());

        List<Quiz> quizList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList1.get(0).getActivity_id()).collect(Collectors.toList());

        List<Activity> activityList2 = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Poll")).collect(Collectors.toList());

        List<Quiz> surveyList = quizMakerService.findAllQuiz().stream().filter(x-> x.getActivity().getActivity_id()==activityList2.get(0).getActivity_id()).collect(Collectors.toList());


        List<QuizDetails> upcomingQuiz = new ArrayList<QuizDetails>();
        List<QuizDetails> activeQuiz = new ArrayList<QuizDetails>();
        List<QuizDetails> expiredQuiz = new ArrayList<QuizDetails>();

        List<QuizDetails> upcomingSurvey = new ArrayList<QuizDetails>();
        List<QuizDetails> activeSurvey = new ArrayList<QuizDetails>();


        List<Object[]> userDetailsObject = quizMakerService.getUserDetailsById(userId);
        for(Object[] objects : userDetailsObject) {

            city = objects[0].toString();
            role= objects[1].toString();

        }
        for(Quiz quiz : quizList){

            List<String> cityLIst = Arrays.asList(quiz.getAssigneeCityList()!=null?quiz.getAssigneeCityList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
            List<String> roleLIst = Arrays.asList(quiz.getAssigneeCityList()!=null?quiz.getAssigneeRoleList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
            if(cityLIst.contains(city) && roleLIst.contains(role)){
                QuizDetails quizDetails = new QuizDetails();
                if(getDaysDifference(currentDate,quiz.getDate_schedule()) > 5){

                    quizDetails.setQuizTitle(quiz.getQuiz_title());
                    quizDetails.setLevel(quiz.getLevel());
                    quizDetails.setQuizCategory(quiz.getCategory().getCateogry_title());
                    quizDetails.setStatus(quiz.getStatus());
                    quizDetails.setDate(quiz.getDate_schedule());
                    quizDetails.setQuizIndex(quiz.getQuizIndex());
                    quizDetails.setMax_attempt_left(getQuizAttempt(userId.intValue(),quiz.getQuizIndex())!=-1?getQuizAttempt(userId.intValue(),quiz.getQuizIndex()):quiz.getMax_attempt());
                    expiredQuiz.add(quizDetails);
                }
                else if(getDaysDifference(currentDate,quiz.getDate_schedule()) <= 5 && getDaysDifference(currentDate,quiz.getDate_schedule())>=0){

                    quizDetails.setQuizTitle(quiz.getQuiz_title());
                    quizDetails.setLevel(quiz.getLevel());
                    quizDetails.setQuizCategory(quiz.getCategory().getCateogry_title());
                    quizDetails.setStatus(quiz.getStatus());
                    quizDetails.setDate(quiz.getDate_schedule());
                    quizDetails.setQuizIndex(quiz.getQuizIndex());
                    quizDetails.setMax_attempt_left(getQuizAttempt(userId.intValue(),quiz.getQuizIndex())!=-1?getQuizAttempt(userId.intValue(),quiz.getQuizIndex()):quiz.getMax_attempt());
                    activeQuiz.add(quizDetails);
                }
                else if (getDaysDifference(currentDate,quiz.getDate_schedule()) < 0){

                    quizDetails.setQuizTitle(quiz.getQuiz_title());
                    quizDetails.setLevel(quiz.getLevel());
                    quizDetails.setQuizCategory(quiz.getCategory().getCateogry_title());
                    quizDetails.setStatus(quiz.getStatus());
                    quizDetails.setDate(quiz.getDate_schedule());
                    quizDetails.setQuizIndex(quiz.getQuizIndex());
                    quizDetails.setMax_attempt_left(getQuizAttempt(userId.intValue(),quiz.getQuizIndex())!=-1?getQuizAttempt(userId.intValue(),quiz.getQuizIndex()):quiz.getMax_attempt());
                    upcomingQuiz.add(quizDetails);
                }


            }
            eligibility.setActiveQuizDetailsList(activeQuiz);
            eligibility.setExpiredQuizDetailsList(expiredQuiz);

            eligibility.setUpcomingQuizDetailsList(upcomingQuiz);

        }

        for(Quiz quiz : surveyList){

            List<String> cityLIst = Arrays.asList(quiz.getAssigneeCityList()!=null?quiz.getAssigneeCityList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
            List<String> roleLIst = Arrays.asList(quiz.getAssigneeCityList()!=null?quiz.getAssigneeRoleList().split("\\s*,\\s*"):"All".split("\\s*,\\s*"));
            if(cityLIst.contains(city) && roleLIst.contains(role)){
                QuizDetails quizDetails = new QuizDetails();
                if(getDaysDifference(currentDate,quiz.getDate_schedule()) <= 5 && getDaysDifference(currentDate,quiz.getDate_schedule())>=0){

                    quizDetails.setQuizTitle(quiz.getQuiz_title());

                    quizDetails.setStatus(quiz.getStatus());
                    quizDetails.setDate(quiz.getDate_schedule());
                    quizDetails.setSurveyIndex(quiz.getSurveyIndex());
                    quizDetails.setSurveyParticipated(getSurveyPartcipated(userId.intValue(),quiz.getSurveyIndex()));
                    activeSurvey.add(quizDetails);
                }
                else if (getDaysDifference(currentDate,quiz.getDate_schedule()) < 0){

                    quizDetails.setQuizTitle(quiz.getQuiz_title());
                    quizDetails.setStatus(quiz.getStatus());
                    quizDetails.setDate(quiz.getDate_schedule());
                    quizDetails.setSurveyIndex(quiz.getSurveyIndex());
                    quizDetails.setSurveyParticipated(getSurveyPartcipated(userId.intValue(),quiz.getSurveyIndex()));
                    upcomingSurvey.add(quizDetails);
                }


            }
            eligibility.setActiveSurveyList(activeSurvey);


            eligibility.setUpcomingSurveyList(upcomingSurvey);

        }

        return eligibility;

    }

    @RequestMapping(value = "/getSurveyParticipated/{userId}/{surveyId}", method = RequestMethod.GET)
    public boolean getSurveyPartcipated(@PathVariable("userId") int userId, @PathVariable("surveyId") int surveyIndex) {

        LOG.info("********** quizMakerService ***** ",quizMakerService);
        SurveyUser participated =
                quizMakerService.getSurveyPartcipate(Long.valueOf(userId),surveyIndex);
        return participated!=null?participated.isSurveyParticipated():false;
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
    public  List<QBankCategoryMap>  getQuestionBankCount() {

        List<QBankCategoryMap> questionBankMap = new ArrayList<QBankCategoryMap>();

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


    @RequestMapping(value = "/getResultByCategory/{categoryId}", method = RequestMethod.GET)
    public  CategoryResult  getResultByCategory(@PathVariable("categoryId") Long categoryId) {

        CategoryResult categoryResult = new CategoryResult();
        List<QuizUser>  quizUserList = quizMakerService.getQuizUserList(categoryId);

        // Count the pass
        long passCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("PASS"))
                .count();
        // Count the pass
        long failCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("FAIL"))
                .count();

        categoryResult.setPassCount(passCount);
        categoryResult.setFailCount(failCount);
        categoryResult.setCategoryId(categoryId);

        return categoryResult;

    }

    @RequestMapping(value = "/getAllResult", method = RequestMethod.GET)
    public  CategoryResult  getAllResult() {

        CategoryResult categoryResult = new CategoryResult();
        List<QuizUser>  quizUserList = quizMakerService.findAllQuizResult();

        // Count the pass
        long passCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("PASS"))
                .count();
        // Count the pass
        long failCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("FAIL"))
                .count();

        categoryResult.setPassCount(passCount);
        categoryResult.setFailCount(failCount);


        return categoryResult;

    }
    @RequestMapping(value = "/getSurveyParticipatedCount/{surveyId}", method = RequestMethod.GET)

    public  long  getSurveyParticipatedCount(@PathVariable("surveyId") int surveyId) {

        List<SurveyUser> surveyUserList  = quizMakerService.findAllSurveyUser();

        long surveyParticipatedUserCount = surveyUserList.stream()
                .filter(x -> x.getSurvey_id() == surveyId && x.isSurveyParticipated())
                .count();
        return surveyParticipatedUserCount;

    }

    @RequestMapping(value = "/getResultByQuizId/{quizId}", method = RequestMethod.GET)
    public  CategoryResult  getResultByQuizId(@PathVariable("quizId") int quizId) {

        CategoryResult categoryResult = new CategoryResult();
        List<QuizUser>  quizUserList = quizMakerService.getQuizResultByQuizId(quizId);

        // Count the pass
        long passCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("PASS"))
                .count();
        // Count the pass
        long failCount = quizUserList.stream()
                .filter(x -> x.getResult().equals("FAIL"))
                .count();

        categoryResult.setPassCount(passCount);
        categoryResult.setFailCount(failCount);


        return categoryResult;

    }


    @RequestMapping(value = "/getCitywiseResult/{state}", method = RequestMethod.GET)
    public  List<CategoryResult>  getResultByQuizId(@PathVariable("state") String state) {

        List<CategoryResult> categoryResultList = new ArrayList<CategoryResult>();
        CategoryResult categoryResult = null;
        for(Object[] quizUser : quizMakerService.cityWisePassResult(state)){

            categoryResult = new CategoryResult();
            categoryResult.setCity(quizUser[1].toString());
            categoryResult.setState(state);
            categoryResult.setPassCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("pass count,city wise {} {}",quizUser[0],quizUser[1]);
        }
        for(Object[] quizUser : quizMakerService.cityWiseFailResult(state)){

            categoryResult = new CategoryResult();
            categoryResult.setCity(quizUser[1].toString());
            categoryResult.setState(state);
            categoryResult.setFailCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("fail count,city wise {} {}",quizUser[0],quizUser[1]);
        }

        return categoryResultList;

    }

    @RequestMapping(value = "/getStatewiseResult", method = RequestMethod.GET)
    public  List<CategoryResult> getStatewiseResult() {

        List<CategoryResult> categoryResultList = new ArrayList<CategoryResult>();
        CategoryResult categoryResult = null;
        for(Object[] quizUser : quizMakerService.stateWisePassResult()){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setPassCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("pass count,state {} {}",quizUser[0],quizUser[1]);
        }
        for(Object[] quizUser : quizMakerService.stateWiseFailResult()){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setFailCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("fail count,state {} {}",quizUser[0],quizUser[1]);
        }

        return categoryResultList;
    }

    @RequestMapping(value = "/getCityWiseResult/{quizId}/{state}", method = RequestMethod.GET)
    public  List<CategoryResult> getStatewiseResult(@PathVariable("quizId") int quizId , @PathVariable("state") String state) {

        List<CategoryResult> categoryResultList = new ArrayList<CategoryResult>();
        CategoryResult categoryResult = null;
        for(Object[] quizUser : quizMakerService.getPassByQuizIdCityWise(quizId,state)){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setPassCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("pass count,state {} {}",quizUser[0],quizUser[1]);
        }
        for(Object[] quizUser : quizMakerService.getFailByQuizIdCityWise(quizId,state)){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setFailCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("fail count,state {} {}",quizUser[0],quizUser[1]);
        }

        return categoryResultList;
    }



    @RequestMapping(value = "/getStatewiseResultByCategory/{categoryId}", method = RequestMethod.GET)
    public  List<CategoryResult> getStatewiseResultByCategory(@PathVariable("categoryId") Long categoryId) {

        List<CategoryResult> categoryResultList = new ArrayList<CategoryResult>();
        CategoryResult categoryResult = null;
        for(Object[] quizUser : quizMakerService.getStateWisePassCountByCategory(categoryId)){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setPassCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("pass count,state {} {}",quizUser[0],quizUser[1]);
        }
        for(Object[] quizUser : quizMakerService.getStateWiseFailCountByCategory(categoryId)){

            categoryResult = new CategoryResult();
            categoryResult.setState(quizUser[1].toString());
            categoryResult.setFailCount(((BigInteger)quizUser[0]).longValue());
            categoryResultList.add(categoryResult);
            LOG.info("fail count,state {} {}",quizUser[0],quizUser[1]);
        }

        return categoryResultList;
    }


    @RequestMapping(value = "/getActivity/{userid}", method = RequestMethod.GET)
    public ActivityLog activityLog(@PathVariable("userid") Long userId) {
        return quizMakerService.getActivityByUserId(userId);
    }

    @RequestMapping(value = "/getQuizUserByCategory/{categoryId}", method = RequestMethod.GET)
    public List<String> getQuizUserByCategory(@PathVariable("categoryId") Long categoryId) {

        List<String> quizList = new ArrayList<String>();

        for (Object[] quizObject : quizMakerService.getQuizUserByCategory(categoryId)){
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
    @RequestMapping(value = "/deleteSurvey/{id}", method = RequestMethod.GET)
    public ResponseEntity removeSurvey( @PathVariable("id") int id) {
        quizMakerService.removeSurveyById(id);
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


    @RequestMapping(value = "/getQuizCategory/{id}", method = RequestMethod.GET)
    @Transactional

    public ResponseEntity fetchQuizCategory(@PathVariable("id") Long quizId) {



        Optional<Quiz> quiz = quizMakerService.getQuizCategoryById(quizId);
        LOG.info("QUiz Category"+ quiz.get().getCategory().getCateogry_title());

        return new ResponseEntity("Quiz category is "+quiz.get().getCategory().getCateogry_title(), HttpStatus.OK);
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

    @RequestMapping(value = "/surveyList", method = RequestMethod.GET)
    public List<Quiz> getSurveyList() {

        List<Activity> activityList = quizMakerService.findAll().stream().filter(x -> x.getActivity_title().equals("Poll")).collect(Collectors.toList());

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

    @RequestMapping(value = "/getSurveyByIndex/{surveyIndex}", method = RequestMethod.GET)
    public Quiz getSurveyByIndex(@PathVariable("surveyIndex") int surveyIndex ) {


        Quiz quiz = new Quiz();
        List<Quiz> quizList = quizMakerService.findAllQuiz().stream()
                .filter(x -> x.getSurveyIndex()== surveyIndex).collect(Collectors.toList());
        if(quizList.size()>0) {
            return quizList.get(0);
        }else {
            quiz.setIntroduction_message("Survey Not Avaialble");
            return  quiz;
        }

    }

    @RequestMapping(value = "/getReviewer/{id}", method = RequestMethod.GET)
    @Transactional

    public Optional<User> getReviewer(@PathVariable("id") Long quizId) {

        return quizMakerService.getReviewer(quizId);
    }

    @RequestMapping(value = "/getQuestionSet/{id}", method = RequestMethod.GET)
    @Transactional

    public Set<Question> fetQuestionSetByQuiz(@PathVariable("id") Long quizId) {

        return quizMakerService.fetchQuestionSet(quizId);
    }


}
