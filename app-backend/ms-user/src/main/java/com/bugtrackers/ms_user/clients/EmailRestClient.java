package com.bugtrackers.ms_user.clients;

import java.util.HashMap;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bugtrackers.ms_user.dto.request.EmailRequest;


@FeignClient(name = "ms-email", url="http://localhost:8004/email")
public interface EmailRestClient {
    
    @PostMapping("/send")
    HashMap<String, String> sendEmail(@RequestBody EmailRequest emailRequest);
}
