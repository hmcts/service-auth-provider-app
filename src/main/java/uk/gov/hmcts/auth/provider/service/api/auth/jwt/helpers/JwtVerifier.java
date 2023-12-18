package uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers;

import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenExpiredException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenSignatureException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.UnmappedTokenException;

import java.security.Key;

@SuppressWarnings("deprecation") // will follow up in a specific PR for upgrading
public abstract class JwtVerifier {

    private static final Logger log = LoggerFactory.getLogger(JwtVerifier.class);

    public static String verifyAndExtractSubject(String jwt, Clock clock, String signatureVerificationKey) {
        return verifyAndExtractSubject(jwt, Jwts
            .parser().clock(clock).setSigningKey(signatureVerificationKey).build());
    }

    public static String verifyAndExtractSubject(String jwt, Clock clock, Key signatureVerificationKey) {
        return verifyAndExtractSubject(jwt, Jwts
            .parser().clock(clock).setSigningKey(signatureVerificationKey).build());
    }

    private static String verifyAndExtractSubject(String jwt, JwtParser jwtParser) {
        try {
            return jwtParser
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();

        } catch (SignatureException e) {
            log.warn("Invalid token signature", e);
            throw new TokenSignatureException(e);
        } catch (ExpiredJwtException e) {
            log.info("Token expired", e);
            throw new TokenExpiredException(e);
        } catch (JwtException e) {
            log.error("Invalid token: " + jwt, e);
            throw new UnmappedTokenException(e);
        }
    }
}
