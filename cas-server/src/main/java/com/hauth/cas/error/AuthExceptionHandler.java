package com.hauth.cas.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/23 0:52
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception) {
        log.error("Server Error: ", exception);
        return "Server Error";
    }

}
