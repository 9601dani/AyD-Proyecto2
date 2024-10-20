package com.bugtrackers.ms_user.clients;

import com.bugtrackers.ms_user.dto.request.AuthRequest;
import com.bugtrackers.ms_user.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-auth", url = "http://localhost:8000/auth")
public interface AuthRestClient {

    @PostMapping("/register")
    AuthResponse register(@RequestBody AuthRequest user);

}
