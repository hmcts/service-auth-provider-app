package uk.gov.hmcts.auth.provider.service.api;

import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

public class SignInTest extends BaseFunctionalTest {

    @Test
    public void should_return_200_when_valid_credentials_are_provided() throws Exception {

        signInWith(new SignIn(this.serviceName, otp(this.serviceSecret)))
            .then()
            .assertThat()
            .statusCode(200);
    }

    @Test
    public void should_return_401_when_invalid_credentials_are_provided() throws Exception {

        signInWith(new SignIn(this.serviceName, otp("ABCDEFGHIJKL")))
            .then()
            .assertThat()
            .statusCode(401);
    }
}
