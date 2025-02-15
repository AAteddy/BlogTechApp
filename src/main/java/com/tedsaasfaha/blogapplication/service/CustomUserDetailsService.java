
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username : " + username)); //add to global exception handler

        return new CustomUserPrinciple(user);

    }
}
//
