package com.example.superquizapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Quiz implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String quiz_title;

    private int max_attempt;

    private int passMark;

    private int randomQuestions;

    private String status;

    private String level;

    private int quizIndex;

    private String  date_schedule;

    private String  time_schedule;

    private String  scheduleDateTime;

    private Long creator;

    private boolean show_answer;

    private boolean show_score;

    private String assigneeUserList;

    private String assigneeRoleList;

    private String assigneeClassList;

    private String introduction_message;

    private String conclusion_message;

    /** Time in Min**/
    private int quiz_time;

    @OneToOne(cascade = CascadeType.MERGE)
    private Topic topic;

    @OneToMany(mappedBy = "quiz",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Question> questionList;

    @OneToMany(mappedBy = "quiz" ,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JsonManagedReference

    private List<QuizUserMap> quizUserMapList;

    @OneToOne(cascade = CascadeType.ALL)
    private QuizSetting quizSetting;

    @OneToOne(cascade = CascadeType.MERGE)
    private Activity activity;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;

    public Long getCreatorId() {
        return creator;
    }

    public void setCreatorId(Long creatorId) {
        this.creator = creatorId;
    }
}
