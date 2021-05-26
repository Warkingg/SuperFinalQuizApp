package com.example.superquizapp.model;

import com.example.superquizapp.domain.Audit;
import lombok.Data;

import java.util.List;

@Data
public class ActivityLog {
    private List<Audit> auditing;
}
