package com.bugtrackers.ms_auth.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.exceptions.UserNotCreatedException;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.repositories.AuthRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(AuthRequest user){
        Optional<User> existingUser = authRepository.findByEmail(user.email());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("Email already exists");
        }
        existingUser = authRepository.findByUsername(user.username());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("Username already exists");
        }
        User newUser = new User();
        newUser.setEmail(user.email());
        newUser.setUsername(user.username());

        newUser.setPassword(passwordEncoder.encode(user.password()));

        String jwtToken = this.tokenService.getToken(newUser);
        newUser.setAuthToken(jwtToken);

        User userSaved = authRepository.save(newUser);
        AuthResponse response = new AuthResponse(userSaved);
        return response;
    }

}
