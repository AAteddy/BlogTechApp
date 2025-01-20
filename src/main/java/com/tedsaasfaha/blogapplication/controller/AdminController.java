
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.AdminRegistrationDTO;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.exception.UserNotFoundException;
import com.tedsaasfaha.blogapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{userId}/assign-role")
    public ResponseEntity<String> assignRoleToUser(
            @PathVariable Long userId,
            @RequestParam String role) {

        User user = userService.findUserById(userId);
        if (user == null)
            throw new UserNotFoundException("User not found with Id = " + userId);

        try {
            userService.assignRole(user, Role.valueOf(role.toUpperCase()));
            return ResponseEntity.ok("Role updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
        }
    }
}
//