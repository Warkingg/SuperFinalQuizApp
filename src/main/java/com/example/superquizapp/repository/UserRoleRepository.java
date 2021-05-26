package com.example.superquizapp.repository;

import com.example.superquizapp.domain.security.UserRole;
import com.example.superquizapp.utility.QuizQueryConstant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends CrudRepository<UserRole,Long> {
    @Query(value= QuizQueryConstant.DELETE_EXISTING_ROLE_BY_ID,nativeQuery = true)
    @Modifying
    void deleteRoleByUserId(@Param("userId") Long userId);
}
