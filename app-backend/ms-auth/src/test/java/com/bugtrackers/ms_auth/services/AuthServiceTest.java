package com.bugtrackers.ms_auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.request.LoginRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.exceptions.UserNotAllowedException;
import com.bugtrackers.ms_auth.exceptions.UserNotCreatedException;
import com.bugtrackers.ms_auth.exceptions.UserNotFoundException;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.repositories.AuthRepository;

public class AuthServiceTest {
    
    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authRequest = new AuthRequest("email@example.com", "username", "password");
        mockUser = new User(1,"email@example.com", "username", "password", "token", true, true,LocalDateTime.now());
        authResponse = new AuthResponse(mockUser);
    }

    @Test
    void shouldRegisterNewUser() {

        User mockUserSaved = new User("email@example.com", "username", "password");
        

        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        when(authRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(passwordEncoder.encode("password")).thenReturn("encoded_password");

        AuthResponse response = authService.register(authRequest);

        assertNotNull(response);
        assertEquals(authResponse.email(), response.email());
        assertEquals(authResponse.username(), response.username());
    }

    @Test
    void shouldNotRegisterNewUserFindByUsername() {
        when(authRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotCreatedException.class, () -> {
            authService.register(authRequest);
        });

        String expectedMessage = "El nombre de usuario ya existe.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotRegisterNewUserFindByEmail() {
        when(authRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        Exception exception = assertThrows(UserNotCreatedException.class, () -> {
            authService.register(authRequest);
        });

        String expectedMessage = "El email ya existe.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindUserByUsername() {
        when(authRepository.findByUsernameOrEmail("username", "username")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("username", "password");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), "username");
        assertEquals(response.email(), "email@example.com");
        assertTrue(response.isActivated());
        assertTrue(response.isVerified());
    }

    @Test 
    void shouldFindUserByEmail() {
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), "username");
        assertEquals(response.email(), "email@example.com");
        assertTrue(response.isActivated());
        assertTrue(response.isVerified());
    }
    
    @Test
    void shouldNotFindUserByEmail() {
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "Usuario no encontrado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotBeCorrectPassword() {
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(false);

        loginRequest = new LoginRequest("email@example.com", "password");

        
        Exception exception = assertThrows(UserNotAllowedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "La contraseña no es correcta.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindUserNotActivated() {
        mockUser.setIsActivated(false);
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        
        Exception exception = assertThrows(UserNotAllowedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "El usuario está deshabilitado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindUserNotVerified() {
        mockUser.setIsVerified(false);
        when(authRepository.findByUsernameOrEmail("email@example.com", "email@example.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        loginRequest = new LoginRequest("email@example.com", "password");

        
        Exception exception = assertThrows(UserNotAllowedException.class, () -> {
            authService.login(loginRequest);
        });

        String expectedMessage = "El usuario no se encuentra verificado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
