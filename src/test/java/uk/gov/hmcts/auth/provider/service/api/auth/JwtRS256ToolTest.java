package uk.gov.hmcts.auth.provider.service.api.auth;

import com.google.common.io.Resources;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtRS256Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.helpers.KeysHelper;

import java.time.Duration;
import java.util.Date;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtRS256ToolTest {

    private static final int TTL = 60;

    @Test
    public void should_issue_expected_token() throws Exception {
        // given
        String privateKey = Resources.toString(getResource("sampleKeys/private"), UTF_8);
        String publicKey = Resources.toString(getResource("sampleKeys/public"), UTF_8);

        JwtRS256Tool jwtTool = new JwtRS256Tool(privateKey, publicKey, TTL);

        // when
        String generatedToken = jwtTool.issueTokenForSubject("cmc");

        Jws<Claims> jws = Jwts.parser()
            .verifyWith(KeysHelper.Public.fromBase64(publicKey))
            .build().parseSignedClaims(generatedToken);

        // then
        assertThat(jws.getHeader().getAlgorithm()).isEqualTo(SignatureAlgorithm.RS256.getValue());
        assertThat(jws.getBody().getSubject()).isEqualTo("cmc");
        assertThat(jws.getBody().getExpiration()).isCloseTo(Date.from(now().plus(Duration.ofSeconds(TTL))), 2_000);
        assertThat(jws.getSignature()).isNotEmpty();
    }

    @Test
    public void should_return_subject_from_verified_token_when_its_valid() throws Exception {
        // given
        String privateKey = Resources.toString(getResource("sampleKeys/private"), UTF_8);
        String publicKey = Resources.toString(getResource("sampleKeys/public"), UTF_8);

        JwtRS256Tool jwtTool = new JwtRS256Tool(privateKey, publicKey, TTL);
        String generatedToken = jwtTool.issueTokenForSubject("cmc");

        // when
        String subject = jwtTool.verifyAndExtractSubject(generatedToken);

        // then
        assertThat(subject).isEqualTo("cmc");
    }
}
