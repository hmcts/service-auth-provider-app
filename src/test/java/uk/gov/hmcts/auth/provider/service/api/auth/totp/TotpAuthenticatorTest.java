package uk.gov.hmcts.auth.provider.service.api.auth.totp;

import com.warrenstrange.googleauth.HmacHashFunction;
import org.junit.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;

public class TotpAuthenticatorTest {
    private static final String SHA1_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";
    private static final String SHA256_SECRET = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQGEZA====";

    @Test
    public void sha256MatchesRfc6238VectorsWithSixDigits() {
        long[] times = {59, 1111111109L, 1111111111L, 1234567890L, 2000000000L, 20000000000L};
        String[] codes = {"119246", "084774", "062674", "819424", "698825", "737706"};
        for (int i = 0; i < times.length; i++) {
            TotpAuthenticator totp = at(times[i]);
            assertThat(totp.issueOneTimePassword(SHA256_SECRET, HmacHashFunction.HmacSHA256)).isEqualTo(codes[i]);
            assertThat(totp.isOneTimePasswordValid(SHA256_SECRET, codes[i], HmacHashFunction.HmacSHA256)).isTrue();
        }
    }

    @Test
    public void upgradedIdentityDoesNotFallBackToSha1() {
        TotpAuthenticator totp = at(59);
        String oldCode = totp.issueOneTimePassword(SHA256_SECRET, HmacHashFunction.HmacSHA1);
        assertThat(totp.isOneTimePasswordValid(SHA256_SECRET, oldCode, HmacHashFunction.HmacSHA256)).isFalse();
    }

    @Test
    public void legacyIdentityStillAcceptsSha1() {
        assertThat(at(59).isOneTimePasswordValid(SHA1_SECRET, "287082")).isTrue();
    }

    @Test
    public void rejectsMalformedAndExpiredCodes() {
        assertThat(at(59).isOneTimePasswordValid(SHA256_SECRET, "invalid", HmacHashFunction.HmacSHA256)).isFalse();
        assertThat(at(3600).isOneTimePasswordValid(SHA256_SECRET, "119246", HmacHashFunction.HmacSHA256)).isFalse();
    }

    private TotpAuthenticator at(long second) {
        return new TotpAuthenticator(Clock.fixed(Instant.ofEpochSecond(second), ZoneOffset.UTC));
    }
}
