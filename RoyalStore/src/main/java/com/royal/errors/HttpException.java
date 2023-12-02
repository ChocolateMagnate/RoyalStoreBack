package com.royal.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class HttpException extends Exception  {
    protected HttpStatus httpErrorCode;
    protected String message;

    public HttpException() {
        super();
    }

    public HttpException(HttpStatus code, String reason) {
        super(reason);
        this.httpErrorCode = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
