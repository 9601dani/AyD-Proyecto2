package com.bugtrackers.gateway.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bugtrackers.gateway.exceptions.NotAllowedException;
import com.bugtrackers.gateway.models.User;
import com.bugtrackers.gateway.repositories.UserRepository;
import com.bugtrackers.gateway.services.TokenService;

import reactor.core.publisher.Mono;

public class VerifyTokenFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private VerifyTokenFilter verifyTokenFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(this.exchange.getRequest()).thenReturn(request);
        when(this.request.getHeaders()).thenReturn(headers);
    }

    @Test
    void shouldApplyWithValidToken() {
        String validToken = "Bearer validToken";
        String username = "testuser";
        String email = "test@example.com";
        
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setAuthToken(validToken);
        when(this.headers.getFirst("Authorization")).thenReturn(validToken);
        when(this.tokenService.isTokenExpired("validToken")).thenReturn(false);
        when(this.tokenService.getClaim("validToken", "username")).thenReturn(username);
        when(this.tokenService.getClaim("validToken", "email")).thenReturn(email);
        when(this.userRepository.findByUsernameAndEmailAndAuthToken(username, email, "validToken")).thenReturn(Optional.of(user));

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);

        verify(this.tokenService).getClaim("validToken", "username");
        verify(this.userRepository).findByUsernameAndEmailAndAuthToken(username, email, "validToken");

    }

    @Test
    void shouldNotFindAuthHeader() {
        when(this.headers.getFirst("Authorization")).thenReturn(null);

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);
        });

        String expectedMessage = "No se encontró el JWT token.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldNotFindBearer() {
        String notValidToken = "Not valid token";

        when(this.headers.getFirst("Authorization")).thenReturn(notValidToken);

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);
        });

        String expectedMessage = "No se encontró el JWT token.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldGetExpiredToken() {
        String validToken = "Bearer validToken";

        when(this.headers.getFirst("Authorization")).thenReturn(validToken);
        when(this.tokenService.isTokenExpired("validToken")).thenReturn(true);

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);
        });

        String expectedMessage = "La sesión ha expirado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldNotFindUser() {
        String validToken = "Bearer validToken";
        String username = "testuser";
        String email = "test@example.com";
        
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setAuthToken(validToken);
        when(this.headers.getFirst("Authorization")).thenReturn(validToken);
        when(this.tokenService.isTokenExpired("validToken")).thenReturn(false);
        when(this.tokenService.getClaim("validToken", "username")).thenReturn(username);
        when(this.tokenService.getClaim("validToken", "email")).thenReturn(email);
        when(this.userRepository.findByUsernameAndEmailAndAuthToken(username, email, "validToken")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);
        });

        String expectedMessage = "No se encontró al usuario.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowJWTVerificationException() {
        String validToken = "Bearer validToken";
        String username = "testuser";
        String email = "test@example.com";
        
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setAuthToken(validToken);
        when(this.headers.getFirst("Authorization")).thenReturn(validToken);
        when(this.tokenService.isTokenExpired("validToken")).thenReturn(false);
        when(this.tokenService.getClaim("validToken", "username")).thenThrow(JWTVerificationException.class);

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            this.verifyTokenFilter.apply(new VerifyTokenFilter.Config()).filter(exchange, chain);
        });

        String expectedMessage = "La sesión ha expirado.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

}
