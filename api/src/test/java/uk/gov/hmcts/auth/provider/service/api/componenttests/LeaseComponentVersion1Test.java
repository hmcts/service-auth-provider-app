package uk.gov.hmcts.auth.provider.service.api.componenttests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;
import uk.gov.hmcts.auth.totp.GoogleTotpAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseComponentVersion1Test extends ComponentTestBase {

    @Value("${auth.provider.service.server.jwtKey}")
    private String jwtKey;
    @Value("${auth.provider.service.server.microserviceKeys.divorce}")
    private String divorceKey;

    @Test
    public void unknownMicroserviceShouldReturn401() throws Exception {
        scenario
            .given()
            .when().leaseVersion1("unknownMicroservice", "anyPassword")
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Unknown microservice")));
    }

    @Test
    public void invalidOneTimePasswordShouldReturn403() throws Exception {
        scenario
            .given()
            .when().leaseVersion1("divorce", "invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Invalid one-time password")));
    }

    @Test
    public void serviceNameShouldBeCaseInsensitive() throws Exception {
        scenario
            .given()
            .when().leaseVersion1("DIvorCE", "invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Invalid one-time password")));
    }

    @Test
    public void validOneTimePasswordShouldReturnJwtToken() throws Exception {
        String validOneTimePassword = new GoogleTotpAuthenticator().issueOneTimePassword(divorceKey);

        scenario
            .given()
            .when().leaseVersion1("divorce", validOneTimePassword)
            .then()
            .token(token -> {
                Claims claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody();
                assertThat(claims.getSubject()).isEqualTo("divorce");
            });
    }
}
