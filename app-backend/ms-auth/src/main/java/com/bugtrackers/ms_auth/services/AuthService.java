package com.bugtrackers.ms_auth.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.request.LoginRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.exceptions.UserNotAllowedException;
import com.bugtrackers.ms_auth.exceptions.UserNotCreatedException;
import com.bugtrackers.ms_auth.exceptions.UserNotFoundException;
import com.bugtrackers.ms_auth.models.Role;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.models.UserInformation;
import com.bugtrackers.ms_auth.models.UserRole;
import com.bugtrackers.ms_auth.repositories.AuthRepository;
import com.bugtrackers.ms_auth.repositories.UserHasRoleRepository;
import com.bugtrackers.ms_auth.repositories.UserInformationRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserHasRoleRepository userHasRoleRepository;
    private final UserInformationRepository userInformationRepository;

    public AuthResponse register(AuthRequest user) {
        Optional<User> existingUser = authRepository.findByEmail(user.email());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("El email ya existe.");
        }
        existingUser = authRepository.findByUsername(user.username());
        if (existingUser.isPresent()) {
            throw new UserNotCreatedException("El nombre de usuario ya existe.");
        }
        User newUser = new User();
        newUser.setEmail(user.email());
        newUser.setUsername(user.username());

        newUser.setPassword(passwordEncoder.encode(user.password()));

        User userSaved = authRepository.save(newUser);

        UserInformation userInformation = new UserInformation();
        userInformation.setUser(userSaved);

        this.userInformationRepository.save(userInformation);

        // asign default role to user
        Role role = new Role();
        // default role
        role.setId(2);
        UserRole userRole = new UserRole();
        userRole.setUser(userSaved);
        userRole.setRole(role);
        this.userHasRoleRepository.save(userRole);

        AuthResponse response = new AuthResponse(userSaved);
        return response;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Optional<User> userOptional = this.authRepository.findByUsernameOrEmail(loginRequest.usernameOrEmail(), loginRequest.usernameOrEmail());

        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado.");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new UserNotAllowedException("La contraseña no es correcta.");
        }

        if(!user.getIsActivated()) {
            throw new UserNotAllowedException("El usuario está deshabilitado.");
        }

        if(!user.getIsVerified()) {
            throw new UserNotAllowedException("El usuario no se encuentra verificado.");
        }

        String token = tokenService.getToken(user);
        user.setAuthToken(token);

        this.authRepository.save(user);
        AuthResponse response = new AuthResponse(user);

        return response;
    }

}
