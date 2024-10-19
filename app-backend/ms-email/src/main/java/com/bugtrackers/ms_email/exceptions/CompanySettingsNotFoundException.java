package com.bugtrackers.ms_email.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CompanySettingsNotFoundException extends RuntimeException {

    public CompanySettingsNotFoundException(String message) {
        super(message);
    }
    
}
