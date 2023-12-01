package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.royal.auth.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class EmailAndPasswordLoginFilter extends OncePerRequestFilter {
    @Autowired
    private Jwt jwt;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            token = token.substring(7);
            Optional<JWTClaimsSet> claims = jwt.tryGetClaims(request);
            if ((claims.isPresent() && jwt.tokenIsExpired(claims.get())) || jwt.tokenIsIncorrectlySigned(token))
                response.sendError(HttpStatus.UNAUTHORIZED.value(),
                        "User has logged out, please login again.");
        }
        filterChain.doFilter(request, response);
    }
}
