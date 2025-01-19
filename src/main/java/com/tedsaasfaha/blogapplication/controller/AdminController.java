package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.UserRegistrationDTO;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to create an ADMIN user. Only accessible by an existing ADMIN.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> createAdmin(
            @Validated @RequestBody UserRegistrationDTO registrationDTO) {

        if (!"ADMIN".equalsIgnoreCase(registrationDTO.getRole())) {
            return new ResponseEntity<>(
                    "Only ADMIN role can be created via this endpoint.",
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());
        user.setRole(Role.ADMIN);

        userService.registerUser(user);

        return new ResponseEntity<>(
                "Admin user created successfully",
                HttpStatus.CREATED);
    }
}
