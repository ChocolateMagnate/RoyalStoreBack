package com.royal.errors.http;

import com.royal.errors.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class BadAuthorizationException extends HttpException {
    public BadAuthorizationException(String reason) {
        super(reason);
    }
}
