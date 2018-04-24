package uk.gov.hmcts.auth.provider.service.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

public class SignInTest {

    private String testUrl;
    private String serviceName;
    private String serviceSecret;

    @Before
    public void setUp() {
        Config conf = ConfigFactory.load();

        this.testUrl = conf.getString("test-url");
        this.serviceName = conf.getString("test-service-name");
        this.serviceSecret = conf.getString("test-service-secret");
    }

    @Test
    public void should_return_200_when_valid_credentials_are_provided() throws Exception {

        signInWith(new SignIn(this.serviceName, otp(this.serviceSecret)))
            .then()
            .assertThat()
            .statusCode(200);
    }

    @Test
    public void should_return_401_when_invalid_credentials_are_provided() throws Exception {

        signInWith(new SignIn(this.serviceName, otp("INVALIDSECRET123")))
            .then()
            .assertThat()
            .statusCode(401);
    }

    private Response signInWith(SignIn creds) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.testUrl)
            .contentType(ContentType.JSON)
            .body(creds)
            .when()
            .post("/lease");
    }

    private String otp(String secret) {
        return new TotpAuthenticator().issueOneTimePassword(secret);
    }
}
