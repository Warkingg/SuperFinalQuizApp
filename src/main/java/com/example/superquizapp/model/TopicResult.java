package com.example.superquizapp.model;

import lombok.Data;

@Data
public class TopicResult {
    private Long passCount;

    private Long failCount;

    private Long topicId;

    private String classroom;

}
