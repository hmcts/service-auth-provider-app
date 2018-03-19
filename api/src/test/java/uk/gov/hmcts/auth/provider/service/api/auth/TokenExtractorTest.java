package uk.gov.hmcts.auth.provider.service.api.auth;

import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidAuthHeaderException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TokenExtractorTest {

    @Test
    public void should_extract_token() {
        String bearerToken = "Bearer abcd1234";

        assertThat(TokenExtractor.fromBearerToken(bearerToken))
            .isEqualTo("abcd1234");
    }

    @Test
    public void should_throw_exception_if_prefix_is_missing() {
        assertThatExceptionOfType(InvalidAuthHeaderException.class)
            .isThrownBy(() -> TokenExtractor.fromBearerToken("woohoo"));
    }
}
