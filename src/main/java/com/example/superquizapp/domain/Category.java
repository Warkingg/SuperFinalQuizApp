package com.example.superquizapp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long category_id;

    private String cateogry_title;

    private String category_desc;

    @OneToOne(cascade = CascadeType.ALL)
    private Audit audit;


}
