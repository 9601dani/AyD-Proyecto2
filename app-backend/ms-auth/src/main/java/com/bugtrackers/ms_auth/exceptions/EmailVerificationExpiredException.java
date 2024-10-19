package com.bugtrackers.ms_auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class EmailVerificationExpiredException extends RuntimeException {
    
    public EmailVerificationExpiredException(String message) {
        super(message);
    }
}
