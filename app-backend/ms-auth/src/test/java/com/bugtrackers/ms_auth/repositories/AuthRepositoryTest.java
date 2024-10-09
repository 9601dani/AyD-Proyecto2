package com.bugtrackers.ms_auth.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_auth.models.User;
import java.util.Optional;

public class AuthRepositoryTest {
    
    @Mock
    private AuthRepository authRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User("email@example.com", "username", "password");
    }

    @Test
    void shouldFindByEmail() {
        when(authRepository.findByEmail("email@example.com")).thenReturn(Optional.of(mockUser));

        Optional<User> result = authRepository.findByEmail("email@example.com");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldNotFindByEmail() {
        when(authRepository.findByEmail("email1@example.com")).thenReturn(Optional.empty());

        Optional<User> result = authRepository.findByEmail("email1@example.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindByUsername() {
        when(authRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));

        Optional<User> result = authRepository.findByUsername("username");

        assertTrue(result.isPresent());
    }

    @Test
    void shouldNotFindByUsername() {
        when(authRepository.findByUsername("username1")).thenReturn(Optional.empty());

        Optional<User> result = authRepository.findByUsername("username1");

        assertTrue(result.isEmpty());
    }
}
