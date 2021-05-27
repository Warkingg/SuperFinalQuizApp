package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class FeedBack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long feedback_id;

    private String feedback;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
