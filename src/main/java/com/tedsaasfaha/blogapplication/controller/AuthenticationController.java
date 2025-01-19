
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.TokenDTO;
import com.tedsaasfaha.blogapplication.dto.UserLoginDTO;
import com.tedsaasfaha.blogapplication.dto.UserRegistrationDTO;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.service.AuthService;
import com.tedsaasfaha.blogapplication.service.UserService;
import com.tedsaasfaha.blogapplication.util.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public AuthenticationController(UserService userService,
                                    AuthService authService,
                                    JwtUtil jwtUtil) {
        this.userService = userService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Validated
            @RequestBody UserRegistrationDTO registrationDTO
            ) {

        // Ensure the role is not ADMIN; only READER or WRITER are allowed
        if (registrationDTO.getRole() != null &&
                registrationDTO.getRole().equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to create users with the ADMIN role.");
        }

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

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(
            @RequestBody TokenDTO tokenDTO) {

        String refreshToken = tokenDTO.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String username = jwtUtil.extractUsername(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        // Generate a new access token
        String newAccessToken = jwtUtil.generateAccessToken(username, role);

        return ResponseEntity.ok(new TokenDTO(newAccessToken, refreshToken));
    }
}
//