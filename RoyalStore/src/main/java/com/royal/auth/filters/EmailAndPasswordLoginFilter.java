package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
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
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterRequest(request, response);
            filterChain.doFilter(request, response);
        } catch (HttpException e) {
            response.sendError(e.getHttpErrorCode().value(), e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization == null;
    }

    private void filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional<JWTClaimsSet> jwtClaimsSet = jwtService.tryGetClaims(request);
        if (jwtClaimsSet.isPresent() && !jwtService.tokenIsExpired(jwtClaimsSet.get())) {
            logger.info("Refreshed a JWT token.");
            SignedJWT jwt = this.jwtService.regenerateExpiringJwtToken(jwtClaimsSet.get());
            response.setHeader("Authorization", "Bearer " + jwt.serialize());
        } else {
            logger.info("Rejected request with invalid JWT token from email and password filter.");
            int errorCode = HttpStatus.UNAUTHORIZED.value();
            String message = "Your session has ended, please login again.";
            response.sendError(errorCode, message);
        }
    }
}