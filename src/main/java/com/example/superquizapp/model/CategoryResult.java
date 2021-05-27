package com.example.superquizapp.model;

import lombok.Data;

@Data
public class CategoryResult {
    private Long passCount;

    private Long failCount;

    private Long categoryId;

    private String city;

    private String state;

}
