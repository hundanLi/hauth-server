package com.hauth.cas.error;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal Argument: ", exception);
        return "Invalid request: " + exception.getMessage();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException exception) {
        log.warn("Resource Not Found: {}", exception.getMessage());
        return "Resource Not Found";
    }

    @ExceptionHandler(InvalidTokenException.class)
    public String handleInvalidTokenException(InvalidTokenException exception,
                                              HttpServletResponse response) {
        log.error("Invalid Token: {}", exception.getToken());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "Invalid Token: "+ exception.getToken();
    }
}
