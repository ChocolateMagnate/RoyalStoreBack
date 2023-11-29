package com.royal.errors.jwt;

import com.royal.errors.HttpException;
import lombok.Getter;

@Getter
public class JwtNotPresentException extends HttpException {
}
