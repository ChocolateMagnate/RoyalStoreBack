package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.royal.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Log4j2
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
        filterRequest(request, response);
        log.info("Reached the end of OAuth2 filter.");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        if (SecurityContextHolder.getContext() != null) return true;
        if (request.getHeader("Authorization") == null) return true;
        for (AntPathRequestMatcher endpoint : publicEndpointMatchers )
            if (endpoint.matches(request)) return true;
        return false;
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
