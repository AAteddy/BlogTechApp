
package com.tedsaasfaha.blogapplication.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRegistrationDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid Email Address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    public void setRole(String role) {
        if (List.of("READER", "WRITER", "ADMIN").contains(role.toUpperCase())) {
            throw new IllegalArgumentException("Invalid role provided.");
        }
        this.role = role;
    }
}
//