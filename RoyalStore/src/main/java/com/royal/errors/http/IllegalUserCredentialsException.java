package com.royal.errors.http;

import com.royal.errors.HttpException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalUserCredentialsException extends HttpException {

    public IllegalUserCredentialsException(String message) {
        this.httpErrorCode = HttpStatus.BAD_REQUEST;
        this.message =  message;
    }
}
