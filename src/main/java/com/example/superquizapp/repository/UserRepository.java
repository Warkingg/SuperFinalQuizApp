package com.example.superquizapp.repository;

import com.example.superquizapp.domain.User;
import com.example.superquizapp.utility.QuizQueryConstant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    User findByUsername(String username);
    @Query("FROM User where email=?1")
    User findByEmail(String email);
    List<User> findAll();
    @Query("FROM User where id=?1")
    User getUserById(Long id);

    @Query(value= QuizQueryConstant.GET_ONLINE_USERS, nativeQuery = true)

    String getOnlineUserById(@Param("userId") Long userId);
}
