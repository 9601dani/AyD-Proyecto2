package com.bugtrackers.ms_img.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileNotCreatedException extends RuntimeException {

    public FileNotCreatedException(String message) {
        super(message);
    }
    
}
