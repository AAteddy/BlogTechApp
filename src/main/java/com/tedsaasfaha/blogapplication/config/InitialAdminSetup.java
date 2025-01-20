package com.tedsaasfaha.blogapplication.config;


import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialAdminSetup {

    public InitialAdminSetup(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setName("Initial Admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("securepassword"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
        }
    }
}
