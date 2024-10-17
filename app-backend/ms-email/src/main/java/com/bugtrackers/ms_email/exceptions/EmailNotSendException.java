package com.bugtrackers.ms_email.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailNotSendException extends RuntimeException {
    
    public EmailNotSendException(String message) {
        super(message);
    }
}
