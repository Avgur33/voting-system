package ru.javaops.topjava2.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;
//423
public class LateTimeException extends AppException{
    public LateTimeException(String msg) {
        super(HttpStatus.LOCKED, msg, ErrorAttributeOptions.of(MESSAGE));
    }
}
