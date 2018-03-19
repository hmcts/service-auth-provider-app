package uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtIssuer {

    public static String issueTokenForSubject(
        String subject,
        SignatureAlgorithm signatureAlgorithm,
        String signingKey,
        Date expirationDate
    ) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(expirationDate)
            .signWith(signatureAlgorithm, signingKey)
            .compact();
    }
}
