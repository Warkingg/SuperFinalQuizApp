package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Topic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long topic_id;

    private String topic_title;

    private String topic_desc;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;


}
