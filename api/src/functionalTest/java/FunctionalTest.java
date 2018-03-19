import io.jsonwebtoken.Jwts;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.AuthService;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionalTest {
    private static final String SOME_JWT_KEY = "wThK0f0/lh3FlxFcL4xUWDMI5C1J9KyQBgXV4wseh1e5J1uYJIjvTvArHxQDrYoHJ23xFxjHkOnvNbR5dXRoxA==";

    private TotpAuthenticator totpAuthenticator = new TotpAuthenticator() {
        @Override
        public String issueOneTimePassword(String base32Key) {
            return null;
        }

        @Override
        public boolean isOneTimePasswordValid(String base32Key, String token) {
            return "valid".equals(token);
        }
    };

    private AuthService authService = new AuthService(
        new JwtHS512Tool(
            SOME_JWT_KEY,
            900,
            Clock.systemUTC()
        ),
        (any) -> new Microservice("id", "key"),
        totpAuthenticator
    );

    @Test
    public void validJwtShouldReturnClaims() {
        String jwt = Jwts.builder()
            .setSubject("divorce")
            .signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, SOME_JWT_KEY)
            .compact();

        assertThat(authService.verify(jwt)).isEqualTo("divorce");
    }
}
