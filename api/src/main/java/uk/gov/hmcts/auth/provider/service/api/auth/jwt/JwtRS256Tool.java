package uk.gov.hmcts.auth.provider.service.api.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers.JwtVerifier;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers.KeysHelper;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.time.Instant.now;

public class JwtRS256Tool implements JwtTool {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private final long ttlInSeconds;

    public JwtRS256Tool(
        String base64encodedPrivateKey,
        String base64encodedPublicKey,
        long ttlInSeconds
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {

        checkArgument(!isNullOrEmpty(base64encodedPrivateKey), "Private key is required");
        checkArgument(!isNullOrEmpty(base64encodedPublicKey), "Public key is required");

        this.privateKey = KeysHelper.Private.fromBase64(base64encodedPrivateKey);
        this.publicKey = KeysHelper.Public.fromBase64(base64encodedPublicKey);
        this.ttlInSeconds = ttlInSeconds;
    }

    @Override
    public String issueTokenForSubject(String subject) {
        return Jwts.builder()
            .setSubject(subject)
            .setExpiration(Date.from(now().plusSeconds(ttlInSeconds)))
            .signWith(SignatureAlgorithm.RS256, this.privateKey)
            .compact();
    }

    @Override
    public String verifyAndExtractSubject(String jwt) {
        return JwtVerifier.verifyAndExtractSubject(jwt, () -> Date.from(now()), publicKey);
    }
}
