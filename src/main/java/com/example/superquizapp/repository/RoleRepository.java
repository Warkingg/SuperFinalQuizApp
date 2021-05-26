package com.example.superquizapp.repository;

import com.example.superquizapp.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role,Integer>{
    Role findByName(String name);
}
