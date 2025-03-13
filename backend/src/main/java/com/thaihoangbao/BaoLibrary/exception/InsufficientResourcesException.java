package com.thaihoangbao.BaoLibrary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientResourcesException extends RuntimeException {
    public InsufficientResourcesException(String message) {
        super(message);
    }
}
