
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.dto.TokenDTO;
import com.tedsaasfaha.blogapplication.dto.UserLoginDTO;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import com.tedsaasfaha.blogapplication.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

//    public class AuthService() {}


    public TokenDTO login(UserLoginDTO loginDTO) {
        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new TokenDTO(token);
    }
}
//