package com.royal.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.royal.errors.HttpException;
import com.royal.errors.JwtNotPresentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class JwtService {
    @Value("${JWT_SINGING_KEY}")
    private String jwtSingingKey;

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
        return now.isAfter(expiration);
    }

    public SignedJWT generateJwtToken(String subject, boolean rememberMe) throws HttpException {
        JWSSigner signer = null;
        try {
            KeyPair pair = generateKeyPair();
            signer = new ECDSASigner((ECPrivateKey) pair.getPrivate());
            Date expiration = getExpirationTime(Instant.now(), rememberMe);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer("http://localhost:8080")
                    .expirationTime(expiration)
                    .claim("rememberMe", rememberMe)
                    .issueTime(new Date())
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256).keyID("kid").build(), claimsSet);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (JOSEException e) {
            throw new HttpException(HttpStatus.UNAUTHORIZED,
                    "JWT token could not be signed with " + signer);
        }
    }

    public SignedJWT regenerateExpiringJwtToken(JWTClaimsSet claims) throws Exception {
        if (tokenIsExpired(claims)) return null;
        String subject = claims.getSubject();
        var rememberMe = (boolean)claims.getClaim("rememberMe");
        return generateJwtToken(subject, rememberMe);
    }

    private Date getExpirationTime(@NotNull Instant issuedAt, boolean rememberMe) {
        int durationInMinutes = (rememberMe) ? 2 * 64 : 30;
        Instant expiration = issuedAt.plus(durationInMinutes, ChronoUnit.MINUTES);
        return Date.from(expiration);
    }

    @SneakyThrows({NoSuchAlgorithmException.class, InvalidAlgorithmParameterException.class})
    private static KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        return keyPairGenerator.generateKeyPair();
    }
}
