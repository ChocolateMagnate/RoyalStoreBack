package com.royal.errors.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtSignatureException extends RuntimeException {
    private final String invalidJwtToken;
}
