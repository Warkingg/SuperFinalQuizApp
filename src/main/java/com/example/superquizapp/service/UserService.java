package com.example.superquizapp.service;

import com.example.superquizapp.domain.User;
import com.example.superquizapp.domain.security.PasswordResetToken;
import com.example.superquizapp.domain.security.UserRole;
import com.example.superquizapp.model.AssignRoles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserService {
    User createUser (User user, Set<UserRole> userRoles);

    User save (User user);

    Optional<User> findById(Long id);

    User findByUsername (String username);

    User findByEmail(String email);

    PasswordResetToken getPasswordResetToken(final String token);

    void createPasswordResetTokenForUser(final User user, final String token);

    List<User> getUserList();

    Set<UserRole> getUserRole(Long id);

    void assignRoles(AssignRoles assignRoles);

    boolean getOnlineUser(Long userId);
}
