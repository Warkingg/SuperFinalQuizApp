package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activity_id;

    private String activity_title;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;
}
