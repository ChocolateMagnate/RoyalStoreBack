package com.royal.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public abstract class HttpException extends Throwable  {
    protected HttpStatus httpErrorCode;
    protected String message;

    public HttpException() {
        super();
    }

    public HttpException(String reason) {
        super(reason);
    }

}
