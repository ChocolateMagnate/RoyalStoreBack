package com.royal.errors.http;

import com.royal.errors.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserIsLoggedOutException extends HttpException {
    public UserIsLoggedOutException() {
        super("User is logged out, please login again.");
    }
}
