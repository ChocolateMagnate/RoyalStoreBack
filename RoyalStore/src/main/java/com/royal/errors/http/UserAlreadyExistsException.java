package com.royal.errors.http;

import com.royal.errors.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FOUND)
public class UserAlreadyExistsException extends HttpException {
    private final String existingUserCredential;

    public UserAlreadyExistsException(String userCriteria) {
        this.existingUserCredential = userCriteria;
    }

    @Override
    public String getMessage() {
        return "User by email " + existingUserCredential + " already exist.";
    }

}
