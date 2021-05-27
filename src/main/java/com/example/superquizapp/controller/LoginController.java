package com.example.superquizapp.controller;

import com.example.superquizapp.domain.Audit;
import com.example.superquizapp.domain.User;
import com.example.superquizapp.repository.AuditRepository;
import com.example.superquizapp.service.UserService;
import com.example.superquizapp.utility.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private SessionRegistry sessionRegistry;

    @Autowired
    AuditRepository auditRepository;

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

//    @RequestMapping("/login")
//    public ResponseEntity login(
//            @RequestParam(value="error", required = false) String error
//    ) {
//        if (error != null) {
//            return new ResponseEntity("Login failed.",HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity("Login success.",HttpStatus.OK);
//    }

//    @RequestMapping("/")
//    public ResponseEntity logout(
//            @RequestParam("logout") String logout
//    ){
//        return new ResponseEntity("Logout success.", HttpStatus.OK);
//    }
//
//    @RequestMapping(value="login", method = RequestMethod.POST)
//    public ResponseEntity loginPost(@RequestBody User json , HttpSession session , HttpServletRequest request) throws
//            ServletException {
//
//        String message = null;
//        if(json.getUsername() == null || json.getPassword() ==null) {
//            throw new ServletException("Please fill in username and password");
//        }
//
//        String username = json.getUsername();//json.get("username");
//        String password = json.getPassword();//json.get("password");
//
//        User user= userService.findByUsername(username);
//        if (user==null) {
//            //throw new ServletException("User name not found.");
//            message = "User Not Found ";
//        } else {
//            // String encodePassword = SecurityUtility.passwordEncoder().encode("password");
//
//            BCryptPasswordEncoder encoder = SecurityUtility.passwordEncoder();
//            encoder.matches(password, user.getPassword());
//            String pwd = user.getPassword();
//
//            Map loginObject = new HashMap();
//
//            if (!encoder.matches(password, user.getPassword())) {
//                loginObject.put("messsage", "login error!");
//                loginObject.put("token", "");
//                message = "Invalid login :" + session.getId();
//                //return new ResponseEntity(loginObject, HttpStatus.OK);
//                //throw new ServletException("Invalid login. Please check your name and password");
//
//            } else {
//                message = "login success :" + session.getId()+" :"+user.getId();
//
//                Audit addAudit =new Audit();
//                Date date = new Date();
//                addAudit.setAudit_event("Login Event");
//                addAudit.setUserId(user.getId());
//                addAudit.setDate_created(DateFormatter.formatDate(date));
//                addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1]);
//                auditRepository.save(addAudit);
//
//            }
//            loginObject.put("messsage", "login success!");
//            loginObject.put("token", session.getId());
//        }
//        // request.getSession(false).setMaxInactiveInterval(1);
//        request.getSession().setAttribute("NOTES_SESSION", "active");
//        return new ResponseEntity(message, HttpStatus.OK);
//    }

    @RequestMapping(value = "/user/logout/{userId}", method = RequestMethod.POST)
    public String logout(HttpServletRequest request,@PathVariable("userId") Long userId) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        Audit addAudit =new Audit();
        Date date = new Date();
        addAudit.setAudit_event("Logout Event");
        addAudit.setUserId(userId);
        addAudit.setDate_created(DateFormatter.formatDate(date));
        addAudit.setTime_created(DateFormatter.formatDateAndTime(date).split(" ")[1]);
        auditRepository.save(addAudit);


        LOG.info("Logged out successful");
        return "logout success.";
    }

    @GetMapping("/quizLogin")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "redirect:/welcome";
    }

    @GetMapping( "/welcome")
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public User registerUser(@RequestBody User user) {
        return userService.save(user);
    }


    @RequestMapping("/test")
    public String test() {
        return "test";
    }

    @RequestMapping("/checkSession")
    public boolean checkSession(HttpSession session , HttpServletRequest request) {
        boolean activeSession = false;
        if(request.getSession().getAttribute("NOTES_SESSION")!=null){
            LOG.info("Session "+request.getSession().getAttribute("NOTES_SESSION")+" timeout "+request.getSession().getMaxInactiveInterval());
            activeSession = true;
        }else {
            LOG.info("Session "+request.getSession().getAttribute("NOTES_SESSION")+" timeout "+request.getSession().getMaxInactiveInterval());
            activeSession = false;
        }
        return activeSession;
    }


//    @RequestMapping(value = "/getLoggedInUser", method = RequestMethod.GET)
//    @ResponseBody
//    public List<String> currentUserName() {
//        List<Object> principals = sessionRegistry.getAllPrincipals();
//
//        List<String> usersNamesList = new ArrayList<String>();
//
//        for (Object principal: principals) {
//            if (principal instanceof User) {
//                usersNamesList.add(((User) principal).getUsername());
//            }
//        }
//        return usersNamesList;
//    }
    @RequestMapping("/token")
    @ResponseBody
    public Map<String,String> token(HttpSession session) {
        return Collections.singletonMap("token", session.getId());
    }
}

