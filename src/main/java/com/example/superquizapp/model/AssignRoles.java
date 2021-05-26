package com.example.superquizapp.model;

import lombok.Data;

import java.util.List;

@Data
public class AssignRoles {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    private List<Integer> roles;
}
