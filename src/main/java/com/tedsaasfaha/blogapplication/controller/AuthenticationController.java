
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.TokenDTO;
import com.tedsaasfaha.blogapplication.dto.UserLoginDTO;
import com.tedsaasfaha.blogapplication.dto.UserRegistrationDTO;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.service.AuthService;
import com.tedsaasfaha.blogapplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final AuthService authService;

    public AuthenticationController(UserService userService,
                                    AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Validated
            @RequestBody UserRegistrationDTO registrationDTO
            ) {

        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());
        user.setRole(registrationDTO.getRole() != null ?
                Role.valueOf(registrationDTO.getRole().toUpperCase()) :
                Role.READER); // default role is READER

        userService.registerUser(user);
        return new ResponseEntity<>(
                "User registered successfully",
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> loginUser(
            @Validated
            @RequestBody UserLoginDTO loginDTO
            ) {

        TokenDTO token = authService.login(loginDTO);
        return ResponseEntity.ok(token);
    }
}
//