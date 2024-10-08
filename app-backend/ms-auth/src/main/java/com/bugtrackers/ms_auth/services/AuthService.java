package com.bugtrackers.ms_auth.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.repositories.AuthRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public User register(AuthRequest user){
        User existingUser = authRepository.findByEmail(user.email());
        if (existingUser != null) {
            throw new RuntimeException("Email already exists");
        }
        existingUser = authRepository.findByUsername(user.username());
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }
        User newUser = new User();
        newUser.setEmail(user.email());
        newUser.setUsername(user.username());
        newUser.setPassword(user.password());
        return authRepository.save(newUser);
    }

}
