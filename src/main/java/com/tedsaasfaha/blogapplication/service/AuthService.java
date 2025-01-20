
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.dto.TokenDTO;
import com.tedsaasfaha.blogapplication.dto.UserLoginDTO;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import com.tedsaasfaha.blogapplication.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthService(UserRepo userRepo,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public boolean userExist(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public TokenDTO login(UserLoginDTO loginDTO) {
        User user = userRepo.findByEmail(loginDTO.getEmail().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException(
                        "Invalid email or password"));

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.warn("User with username: {} has tried to access with an Invalid password", user.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        // Generate both access and refresh tokens
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return new TokenDTO(accessToken, refreshToken);
    }
}
//