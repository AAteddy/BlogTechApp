
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

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

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    public boolean validatePassword(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword,encodePassword);
    }
}
//