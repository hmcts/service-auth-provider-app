package uk.gov.hmcts.auth.provider.service.api.auth;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidOneTimePasswordException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.TokenSignatureException;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.UnmappedTokenException;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    private static final SecretKey SOME_JWT_KEY = Jwts.SIG.HS512.key().build();

    private TotpAuthenticator totpAuthenticator;
    private AuthService authService;

    @Before
    public void setUp() throws Exception {
        totpAuthenticator = mock(TotpAuthenticator.class);

        authService = new AuthService(
            new JwtHS512Tool(Base64.getEncoder().encodeToString(SOME_JWT_KEY.getEncoded()), 900, Clock.systemUTC()),
            (id) -> new Microservice(id, id),
            totpAuthenticator
        );
    }

    @Test
    public void invalid_totp_should_result_in_exception() {
        given(totpAuthenticator.isOneTimePasswordValid(anyString(), anyString()))
            .willReturn(false);

        Throwable exc = catchThrowable(() -> authService.lease("some_service", "0123"));

        assertThat(exc).isInstanceOf(InvalidOneTimePasswordException.class);
    }

    @Test
    public void valid_totp_should_result_in_valid_jwt_token() {
        given(totpAuthenticator.isOneTimePasswordValid(anyString(), anyString()))
            .willReturn(true);

        String jwt = authService.lease("some_service", "1234");

        assertThat(jwt)
            .isNotEmpty()
            .containsPattern(".+\\..+\\..+");
    }

    @Test
    public void jwt_signed_with_wrong_key_should_throw_an_exception() {
        SecretKey someOtherKey = Jwts.SIG.HS512.key().build();
        String jwt = Jwts.builder().subject("divorce").signWith(someOtherKey).compact();

        Throwable exc = catchThrowable(() -> authService.verify(jwt));

        assertThat(exc).isInstanceOf(TokenSignatureException.class);
    }

    @Test
    public void when_malformed_jwt_is_passed_verify_should_throw_exception() {
        assertThatExceptionOfType(UnmappedTokenException.class)
            .isThrownBy(() -> authService.verify("some malformed token"));
    }

    @Test
    public void when_valid_jwt_is_passed_verify_should_return_service_name() {
        String jwt = Jwts.builder().subject("divorce").signWith(SOME_JWT_KEY).compact();

        assertThat(authService.verify(jwt)).isEqualTo("divorce");
    }
}
