package uk.gov.hmcts.auth.provider.service.api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenExpiredException;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;

import java.time.Clock;
import java.time.Duration;
import java.util.Date;

import static java.time.Clock.systemDefaultZone;
import static java.time.Instant.now;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class JwtHS512ToolTest {

    private static final String JWT_KEY = "jwtKey";
    private static final int TTL = 60;

    @Test
    public void should_issue_expected_token() {
        // given
        JwtHS512Tool jwtTool = new JwtHS512Tool(JWT_KEY, TTL, systemDefaultZone());

        // when
        String generatedToken = jwtTool.issueTokenForSubject("cmc");
        Jws<Claims> jws = Jwts.parser().setSigningKey(JWT_KEY).parseClaimsJws(generatedToken);

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
}
