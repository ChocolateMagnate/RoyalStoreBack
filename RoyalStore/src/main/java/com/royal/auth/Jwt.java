package com.royal.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.errors.jwt.JwtNotPresentException;
import com.royal.errors.jwt.JwtSignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class Jwt {
    private final String jwtSingingKey;

    public Jwt(@Value("${jwt.secret}") String jwtSingingKey) {
        this.jwtSingingKey = jwtSingingKey;
    }

    public static boolean isJwtRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ");
    }

    public static String getJwtToken(HttpServletRequest request) throws JwtNotPresentException {
        String header =  request.getHeader("Authorization");
        if (header == null) throw new JwtNotPresentException();
        return header.substring(7);
    }

    public Optional<JWTClaimsSet> tryGetClaims(HttpServletRequest request) throws JwtSignatureException {
        String token = null;
        try {
            token = getJwtToken(request);
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(this.jwtSingingKey);
            if (!signedJWT.verify(verifier))
                throw new JwtSignatureException(token);
            return Optional.ofNullable(signedJWT.getJWTClaimsSet());
        } catch (JwtNotPresentException | ParseException | JOSEException e) {
            return Optional.empty();
        }


    }

    public boolean tokenIsExpired(@NotNull JWTClaimsSet claims) {
        Instant now = Instant.now();
        var rememberMe = (boolean)claims.getClaim("remember-me");
        int durationInMinutes = (rememberMe) ? 2 * 60 : 30;
        Instant issuedAt = claims.getIssueTime().toInstant();
        Instant expiration = issuedAt.plus(durationInMinutes, ChronoUnit.MINUTES);
        return expiration.isAfter(now);
    }

    public String generateJwtToken(String username, Date expiration) throws Exception {
        KeyPair pair = generateKeyPair();
        JWSSigner signer = new RSASSASigner((RSAPrivateKey) pair.getPrivate());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("http://localhost")
                .expirationTime(expiration)
                .claim("name", "John Doe")
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("kid").build(), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // You may choose a different key size
        return keyPairGenerator.generateKeyPair();
    }
}
