package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class Audit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long audit_id;

    private Long userId;

    private String audit_event;

    private String  date_created;

    private String  time_created;

    private String  date_updated;
    private String  time_updated;

    private boolean showStatus;


}
