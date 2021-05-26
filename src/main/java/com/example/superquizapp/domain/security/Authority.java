package com.example.superquizapp.domain.security;

import java.io.Serializable;

/**
 * Created by shaik on 10/20/16.
 */
public class Authority implements Serializable {


    private static final long serialVersionUID = 353859828906296479L;
    private final String authority;

    public Authority(String authority) {
        this.authority = authority;
    }


}

