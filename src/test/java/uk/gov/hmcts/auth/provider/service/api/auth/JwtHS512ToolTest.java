package uk.gov.hmcts.auth.provider.service.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenExpiredException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenSignatureException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.UnmappedTokenException;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

import static java.time.Clock.systemDefaultZone;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class JwtHS512ToolTest {

    private static final String JWT_KEY = Base64.getEncoder().encodeToString(Jwts.SIG.HS512.key().build().getEncoded());
    private static final int TTL = 60;

    @Test
    public void should_issue_expected_token() {
        // given
        JwtHS512Tool jwtTool = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());

        // when
        String generatedToken = jwtTool.issueTokenForSubject("cmc");
        Jws<Claims> jws = Jwts.parser().setSigningKey(JWT_KEY).build().parseSignedClaims(generatedToken);

        // then
        assertThat(jws.getHeader().getAlgorithm()).isEqualTo(SignatureAlgorithm.HS512.getValue());
        assertThat(jws.getBody().getSubject()).isEqualTo("cmc");
        assertThat(jws.getBody().getExpiration()).isCloseTo(Date.from(now().plus(Duration.ofSeconds(TTL))), 2_000);
        assertThat(jws.getSignature()).isNotEmpty();
    }

    @Test
    public void verification_of_valid_token_should_return_subject() {
        // given
        JwtHS512Tool jwtTool = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());
        String generatedToken = jwtTool.issueTokenForSubject("cmc");

        // when
        String subject = jwtTool.verifyAndExtractSubject(generatedToken);

        // then
        assertThat(subject).isEqualTo("cmc");
    }

    @Test
    public void verification_of_expired_token_should_fail() {
        // given
        JwtHS512Tool jwtToolNow = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());
        JwtHS512Tool jwtToolIn5min = new JwtHS512Tool(JWT_KEY, TTL, Clock.fixed(now().plus(Duration.ofMinutes(5)), systemDefault()));

        // when
        String jwt = jwtToolNow.issueTokenForSubject("cmc");
        Throwable exc = catchThrowable(() -> jwtToolIn5min.verifyAndExtractSubject(jwt));

        // then
        assertThat(exc).isInstanceOf(TokenExpiredException.class);
    }

    @Test
    public void verification_of_invalid_signature_should_fail() {
        // given
        JwtHS512Tool jwtToolNow = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());
        JwtHS512Tool jwtToolIn5min = new JwtHS512Tool(JWT_KEY, TTL, Clock.fixed(now().plus(Duration.ofMinutes(5)), systemDefault()));

        // when
        String jwt = jwtToolNow.issueTokenForSubject("cmc");
        String brokenSignatureJwt = jwt.substring(0, jwt.length() - 1) + "x";
        Throwable exc = catchThrowable(() -> jwtToolIn5min.verifyAndExtractSubject(brokenSignatureJwt));

        // then
        assertThat(exc).isInstanceOf(TokenSignatureException.class);
    }

    @Test
    public void verification_of_invalid_token_should_fail() {
        // given
        JwtHS512Tool jwtToolNow = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());
        JwtHS512Tool jwtToolIn5min = new JwtHS512Tool(JWT_KEY, TTL, Clock.fixed(now().plus(Duration.ofMinutes(5)), systemDefault()));

        // when
        String jwt = jwtToolNow.issueTokenForSubject("cmc");
        String invalidJwt = "x" + jwt;
        Throwable exc = catchThrowable(() -> jwtToolIn5min.verifyAndExtractSubject(invalidJwt));

        // then
        assertThat(exc).isInstanceOf(UnmappedTokenException.class);
    }
}
