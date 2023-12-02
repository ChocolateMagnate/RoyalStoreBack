package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.royal.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class OAuth2Filter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    private final AntPathRequestMatcher[] publicEndpointMatchers = {
            new AntPathRequestMatcher("/register"),
            new AntPathRequestMatcher("/login")
    };

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (shouldFilter(request)) filterRequest(request, response);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        for (AntPathRequestMatcher endpoint : publicEndpointMatchers )
            if (endpoint.matches(request)) return true;
        return false;
    }

    private boolean shouldFilter(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return token != null;
    }

    private void filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<JWTClaimsSet> claims = jwtService.tryGetClaims(request);
        if (claims.isPresent() && jwtService.tokenIsExpired(claims.get())) {
            int errorCode = HttpStatus.UNAUTHORIZED.value();
            String message = "Google authentication has expired, please authenticate through Google again.";
            response.sendError(errorCode, message);
        }
    }
}
