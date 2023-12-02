package com.royal.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.errors.jwt.JwtNotPresentException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class JwtService {
    private final String jwtSingingKey;

    public JwtService(@Value("${jwt.secret}") String jwtSingingKey) {
        this.jwtSingingKey = jwtSingingKey;
    }


    public static boolean isJwtRequest(@NotNull HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ");
    }

    public static @NotNull String getJwtToken(@NotNull HttpServletRequest request) throws JwtNotPresentException {
        if (!isJwtRequest(request)) throw new JwtNotPresentException();
        return request.getHeader("Authorization").substring(7);
    }

    public Optional<JWTClaimsSet> tryGetClaims(HttpServletRequest request) {
        try {
            String token = getJwtToken(request);
            SignedJWT signedJWT = SignedJWT.parse(token);
            return Optional.ofNullable(signedJWT.getJWTClaimsSet());
        } catch (JwtNotPresentException | ParseException e) {
            return Optional.empty();
        }
    }

    public boolean tokenIsIncorrectlySigned(String token) {
        if (token == null) return true;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(this.jwtSingingKey);
            return !signedJWT.verify(verifier);
        } catch (ParseException | JOSEException e) {
            return true;
        }
    }

    public boolean tokenIsExpired(@NotNull JWTClaimsSet claims) {
        Instant now = Instant.now();
        var rememberMe = (boolean)claims.getClaim("rememberMe");
        int durationInMinutes = (rememberMe) ? 2 * 60 : 30;
        Instant issuedAt = claims.getIssueTime().toInstant();
        Instant expiration = issuedAt.plus(durationInMinutes, ChronoUnit.MINUTES);
        return expiration.isAfter(now);
    }

    public SignedJWT generateJwtToken(String subject, Date expiration,
                                      boolean rememberMe) throws JOSEException, NoSuchAlgorithmException {
        KeyPair pair = generateKeyPair();
        JWSSigner signer = new RSASSASigner(pair.getPrivate());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("http://localhost")
                .expirationTime(expiration)
                .claim("rememberMe", rememberMe)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("kid").build(), claimsSet);
        signedJWT.sign(signer);
        return signedJWT;
    }

    public SignedJWT regenerateExpiringJwtToken(JWTClaimsSet claims) throws Exception {
        if (tokenIsExpired(claims)) return null;
        String subject = claims.getSubject();
        var rememberMe = (boolean)claims.getClaim("remember-me");
        int durationInMinutes = (rememberMe) ? 2 * 60 : 30;
        Instant issuedAt = claims.getIssueTime().toInstant();
        Instant newExpirationTime = issuedAt.plus(durationInMinutes, ChronoUnit.MINUTES);
        return generateJwtToken(subject, Date.from(newExpirationTime), rememberMe);
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
