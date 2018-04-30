package uk.gov.hmcts.auth.provider.service.api.auth.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers.JwtIssuer;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers.JwtVerifier;

import java.time.Clock;
import java.util.Date;

public class JwtHS512Tool implements JwtTool {

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private final String jwtKey;
    private final int ttlInSeconds;
    private final Clock clock;

    public JwtHS512Tool(String jwtKey, int ttlInSeconds, Clock clock) {
        this.jwtKey = jwtKey;
        this.ttlInSeconds = ttlInSeconds;
        this.clock = clock;
    }

    public String issueTokenForSubject(String subject) {
        return JwtIssuer.issueTokenForSubject(
            subject,
            SIGNATURE_ALGORITHM,
            jwtKey,
            Date.from(clock.instant().plusSeconds(ttlInSeconds))
        );
    }

    public String verifyAndExtractSubject(String jwt) {
        return JwtVerifier.verifyAndExtractSubject(
            jwt,
            () -> Date.from(clock.instant()),
            jwtKey
        );
    }

}
