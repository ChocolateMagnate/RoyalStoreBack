package com.royal.auth.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.auth.JwtService;
import com.royal.errors.HttpException;
import com.royal.users.domain.User;
import com.royal.users.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Log4j2
public class EmailAndPasswordLoginFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    public EmailAndPasswordLoginFilter(@Autowired JwtService jwtService, @Autowired UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws IOException {
        try {
            boolean shouldContinue = filterRequest(request, response);
            if (shouldContinue) filterChain.doFilter(request, response);
        } catch (HttpException e) {
            response.sendError(e.getHttpErrorCode().value(), e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        logger.trace("Running shouldNotFilter in EmailAndPasswordLoginFilter");
        String authorization = request.getHeader("Authorization");
        if (authorization == null) return true;
        Authentication claimedUser = SecurityContextHolder.getContext().getAuthentication();
        return claimedUser != null && claimedUser.isAuthenticated();
    }

    private boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional<JWTClaimsSet> jwtClaimsSet = jwtService.tryGetClaims(request);
        if (jwtClaimsSet.isPresent() && !jwtService.tokenIsExpired(jwtClaimsSet.get())) {
            User user = userService.loadUserByEmail(jwtClaimsSet.get().getSubject());
            var authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SignedJWT jwt = this.jwtService.regenerateExpiringJwtToken(jwtClaimsSet.get());
            response.setHeader("Authorization", "Bearer " + jwt.serialize());
            log.info("Refreshed a JWT token.");
            return true;
        } else {
            logger.info("Rejected request with invalid JWT token from email and password filter.");
            int errorCode = HttpStatus.UNAUTHORIZED.value();
            String message = "Your session has ended, please login again.";
            response.sendError(errorCode, message);
            return false;
        }
    }
}