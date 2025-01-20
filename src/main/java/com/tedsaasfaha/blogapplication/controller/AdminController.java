
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.AdminRegistrationDTO;
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(
            @Validated @RequestBody AdminRegistrationDTO registrationDTO) {


        User adminUser = new User();
        adminUser.setName(registrationDTO.getName());
        adminUser.setEmail(registrationDTO.getEmail());
        adminUser.setPassword(registrationDTO.getPassword());
        adminUser.setRole(Role.ADMIN);

        userService.registerUser(adminUser);

        return new ResponseEntity<>(
                "Admin user created successfully",
                HttpStatus.CREATED);
    }
}
//