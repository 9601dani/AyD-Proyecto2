package com.bugtrackers.ms_auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotAllowedException extends RuntimeException {

    public UserNotAllowedException(String message) {
        super(message);
    }
    
}
