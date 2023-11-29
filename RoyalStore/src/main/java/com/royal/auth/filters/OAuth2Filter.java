package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.royal.auth.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class OAuth2Filter extends OncePerRequestFilter {
    @Autowired
    private Jwt jwt;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        Optional<JWTClaimsSet> claims = jwt.tryGetClaims(request);
        if (claims.isPresent() && jwt.tokenIsExpired(claims.get()))
            response.sendError(HttpStatus.UNAUTHORIZED.value(),
                    "Google authentication has expired, please authenticate through Google again.");
        filterChain.doFilter(request, response);
    }
/*
    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        RequestMatcher registerMatcher = new AntPathRequestMatcher("/register", HttpMethod.PUT.name());
        RequestMatcher loginMatcher = new AntPathRequestMatcher("/login", HttpMethod.GET.name());
        RequestMatcher negatedRegisterMatcher = new NegatedRequestMatcher(registerMatcher);
        RequestMatcher negatedLoginMatcher = new NegatedRequestMatcher(loginMatcher);
        return !negatedRegisterMatcher.matches(request) || !negatedLoginMatcher.matches(request);
    }*/
}
