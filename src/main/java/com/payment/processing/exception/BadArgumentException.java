package com.payment.processing.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class BadArgumentException extends RuntimeException {

    public BadArgumentException(String message) {
        super(message);
    }
}
