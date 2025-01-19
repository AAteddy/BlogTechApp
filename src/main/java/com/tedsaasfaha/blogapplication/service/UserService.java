
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Transactional
    public void assignRole(User user, Role newRole) {
        Role previousRole = user.getRole();
        user.setRole(newRole);
        userRepo.save(user);

        // Log role change
        logger.info("User {} role changed from {} to {}",
                user.getEmail(), previousRole, newRole);

    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    public boolean validatePassword(String rawPassword,
                                    String encodePassword) {
        return passwordEncoder.matches(rawPassword,encodePassword);
    }
}
//