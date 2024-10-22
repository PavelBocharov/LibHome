package com.mar.ds.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.Payload;
import com.mar.ds.utils.ViewUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public static final String JWT_USER_NAME = "lib_home_user_name";
    public static final String JWT_ISSUER = "LibHome_Issuer";
    public static final String JWT_SUBJECT = "LibHome_Subject";

    public boolean validate(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm())
                .withIssuer(JWT_ISSUER)
                .build();

        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            ViewUtils.showErrorMsg("Not verify JWT token", ex);
            return false;
        }
    }

    public String getUsername(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm())
                .withIssuer(JWT_ISSUER)
                .build();
        Claim claim = verifier.verify(token).getClaim(JWT_USER_NAME);
        return claim.asString();
    }

    public String createJwtToken(String username) {
        return JWT.create()
                .withIssuer(JWT_ISSUER)
                .withSubject(JWT_SUBJECT)
                .withClaim(JWT_USER_NAME, username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000L))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis() + 1000L))
                .sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

}
