package com.royal.errors.http;

import com.royal.errors.HttpException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@AllArgsConstructor
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class IncorrectUserPasswordException extends HttpException {
    private String invalidRawPassword;
}
