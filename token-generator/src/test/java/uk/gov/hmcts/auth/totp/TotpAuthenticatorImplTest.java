package uk.gov.hmcts.auth.totp;

import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TotpAuthenticatorImplTest {

    @Mock
    private IGoogleAuthenticator googleAuthenticator;

    private TotpAuthenticator authenticator;

    @Before
    public void setup() {
        authenticator = new TotpAuthenticatorImpl(googleAuthenticator);
    }

    @Test
    public void calls_IGoogleAuthenticator_instance_when_issuing_password() {
        String secretKey = "very secret key";

        authenticator.issueOneTimePassword(secretKey);

        verify(googleAuthenticator, only()).getTotpPassword(secretKey);
    }

    @Test
    public void calls_IGoogleAuthenticator_instance_when_validating_password() {
        String secretKey = "very secret key";
        String token = "0123456";

        authenticator.isOneTimePasswordValid(secretKey, token);

        verify(googleAuthenticator, only()).authorize(secretKey, Integer.parseInt(token));
    }

    @Test
    public void secret_or_token_is_invalid_if_token_was_not_numeric() {
        String secretKey = "very secret key";
        String token = "0IZ3ASG";

        // even though authenticator is instructed to authorise anything...
        when(googleAuthenticator.authorize(any(), anyInt())).thenReturn(true);

        // ... it should still return false for non-integer token
        assertThat(authenticator.isOneTimePasswordValid(secretKey, token)).isFalse();
    }
}
