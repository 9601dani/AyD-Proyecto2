package com.bugtrackers.gateway.filters;

import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bugtrackers.gateway.exceptions.NotAllowedException;
import com.bugtrackers.gateway.models.User;
import com.bugtrackers.gateway.repositories.UserRepository;
import com.bugtrackers.gateway.services.TokenService;



@Component
public class VerifyTokenFilter extends AbstractGatewayFilterFactory<VerifyTokenFilter.Config> {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public VerifyTokenFilter(
        UserRepository userRepository,
        TokenService tokenService
    ) {
        super(Config.class);
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            try {
                ServerHttpRequest request = exchange.getRequest();
                String authHeader = request.getHeaders().getFirst("Authorization");

                if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new NotAllowedException("No se encontró el JWT token.");
                }

                String token = authHeader.substring(7);
                if(this.tokenService.isTokenExpired(token)) {
                    throw new NotAllowedException("La sesión ha expirado.");
                }

                String username = this.tokenService.getClaim(token, "username");
                String email = this.tokenService.getClaim(token, "email");

                Optional<User> uOptional = this.userRepository.findByUsernameAndEmailAndAuthToken(username, email, token);

                if(uOptional.isEmpty()) {
                    throw new NotAllowedException("No se encontró al usuario.");
                }

                return chain.filter(exchange);
            } catch(JWTVerificationException exception) {
                throw new NotAllowedException("La sesión ha expirado.");
            }
        };
    }

    public static class Config { }
    
}
