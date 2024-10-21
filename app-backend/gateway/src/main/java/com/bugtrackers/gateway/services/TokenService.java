package com.bugtrackers.gateway.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bugtrackers.gateway.exceptions.NotAllowedException;
import com.bugtrackers.gateway.models.CompanySetting;
import com.bugtrackers.gateway.models.User;
import com.bugtrackers.gateway.repositories.CompanySettingRepository;

@Service
public class TokenService {
    
    private final CompanySettingRepository companySettingRepository;
    private final CompanySetting companyName;
    private final CompanySetting jwtSecret;
    private final CompanySetting jwtTime;
    private final CompanySetting zone;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public TokenService(CompanySettingRepository companySettingRepository) {
        this.companySettingRepository = companySettingRepository;
        Optional<CompanySetting> cnOptional = this.companySettingRepository.findByKeyName("company_name");
        Optional<CompanySetting> jsOptional = this.companySettingRepository.findByKeyName("jwt_secret");
        Optional<CompanySetting> jtOptional = this.companySettingRepository.findByKeyName("jwt_time");
        Optional<CompanySetting> zOptional = this.companySettingRepository.findByKeyName("zone");

        
        if(cnOptional.isEmpty() || jsOptional.isEmpty() || jtOptional.isEmpty() || zOptional.isEmpty()) {
            throw new NotAllowedException("No se encontró una configuración.");
        }

        this.companyName = cnOptional.get();
        this.jwtSecret = jsOptional.get();
        this.jwtTime = jtOptional.get();
        this.zone = zOptional.get();

        this.algorithm = Algorithm.HMAC256(jwtSecret.getKeyName());
        this.verifier = JWT.require(algorithm).build();
    }

    public String getToken(User user) {
        return JWT.create()
        .withIssuer(companyName.getKeyValue())
        .withClaim("username", user.getUsername())
        .withClaim("email", user.getEmail())
        .withExpiresAt(getInstant(Integer.valueOf(jwtTime.getKeyValue()), zone.getKeyValue()))
        .sign(this.algorithm);
    }

    public Instant getInstant(Integer minutes, String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return LocalDateTime.now().plusMinutes(minutes).toInstant(zonedDateTime.getOffset());
    }

    public DecodedJWT decodedJWT(String token) throws JWTVerificationException {
        return this.verifier.verify(token);
    }

    public String getClaim(String token, String claimName) throws JWTVerificationException {
        DecodedJWT decodedJWT = this.decodedJWT(token);
        return decodedJWT.getClaim(claimName).asString();
    }

    private LocalDateTime getExpiredAtFromToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = this.decodedJWT(token);
        return decodedJWT.getExpiresAt().toInstant().atZone(ZoneId.of(zone.getKeyValue())).toLocalDateTime();
    }

    public boolean isTokenExpired(String token) throws JWTVerificationException {
        LocalDateTime expiredAt = this.getExpiredAtFromToken(token);
        return LocalDateTime.now().isAfter(expiredAt);
    }
}
