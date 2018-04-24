package uk.gov.hmcts.auth.provider.service.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

public abstract class BaseFunctionalTest {

    protected String testUrl;
    protected String serviceName;
    protected String serviceSecret;

    @Before
    public void setUp() {
        Config conf = ConfigFactory.load();

        this.testUrl = conf.getString("test-url");
        this.serviceName = conf.getString("test-service-name");
        this.serviceSecret = conf.getString("test-service-secret");
    }

    protected Response signInWith(SignIn creds) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.testUrl)
            .contentType(ContentType.JSON)
            .body(creds)
            .when()
            .post("/lease")
            .andReturn();
    }

    protected String otp(String secret) {
        return new TotpAuthenticator().issueOneTimePassword(secret);
    }
}
