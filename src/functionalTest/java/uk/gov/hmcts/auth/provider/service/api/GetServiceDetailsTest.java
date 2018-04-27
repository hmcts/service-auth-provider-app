package uk.gov.hmcts.auth.provider.service.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import uk.gov.hmcts.auth.provider.service.api.model.SignIn;

import static org.hamcrest.Matchers.equalTo;

public class GetServiceDetailsTest extends BaseFunctionalTest {

    @Test
    public void should_return_service_name_when_valid_jwt_token_is_sent() throws Exception {

        String jwt = signInWith(new SignIn(this.serviceName, otp(this.serviceSecret))).asString();

        getDetailsForToken(jwt)
            .then()
            .assertThat()
            .statusCode(200)
            .body(equalTo(this.serviceName));
    }

    @Test
    public void should_return_401_when_invalid_jwt_token_is_sent() throws Exception {
        getDetailsForToken("clearly_not_a_valid_jwt_token")
            .then()
            .assertThat()
            .statusCode(401);
    }

    private Response getDetailsForToken(String jwt) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(this.testUrl)
            .header("Authorization", "Bearer " + jwt)
            .when()
            .get("/details")
            .andReturn();
    }
}
