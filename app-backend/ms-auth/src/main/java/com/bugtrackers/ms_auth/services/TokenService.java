package com.bugtrackers.ms_auth.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bugtrackers.ms_auth.exceptions.SettingNotFoundException;
import com.bugtrackers.ms_auth.models.CompanySetting;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.repositories.CompanySettingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {

    private final CompanySettingRepository companySettingRepository;

    public String getToken(User user) {

        Optional<CompanySetting> companyName = this.companySettingRepository.findByKeyName("company_name");
        Optional<CompanySetting> jwtSecret = this.companySettingRepository.findByKeyName("jwt_secret");
        Optional<CompanySetting> jwtTime = this.companySettingRepository.findByKeyName("jwt_time");
        Optional<CompanySetting> zone = this.companySettingRepository.findByKeyName("zone");

        if(companyName.isEmpty() || jwtSecret.isEmpty() || jwtTime.isEmpty() || zone.isEmpty()) {
            throw new SettingNotFoundException("Couldn't find a company setting");
        }

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.get().getKeyName());
        return JWT.create()
        .withIssuer(companyName.get().getKeyValue())
        .withClaim("username", user.getUsername())
        .withClaim("email", user.getEmail())
        .withExpiresAt(getInstant(Integer.valueOf(jwtTime.get().getKeyValue()), zone.get().getKeyValue()))
        .sign(algorithm);
    }

    private Instant getInstant(Integer minutes, String zone) {
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return LocalDateTime.now().plusMinutes(minutes).toInstant(zonedDateTime.getOffset());
    }
}
