package com.example.superquizapp.model;

import com.example.superquizapp.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfo {

    private String username;
    private boolean status;
    private User role;

}
