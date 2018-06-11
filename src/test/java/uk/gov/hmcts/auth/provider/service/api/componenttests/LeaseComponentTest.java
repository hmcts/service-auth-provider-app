package uk.gov.hmcts.auth.provider.service.api.componenttests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseComponentTest extends ComponentTestBase {

    @Value("${jwtKey}")
    private String jwtKey;
    @Value("${microserviceKeys.divorce}")
    private String divorceKey;

    @Test
    public void unknownMicroserviceShouldReturn401() throws Exception {
        scenario
            .given()
            .when().lease("unknownMicroservice", "anyPassword")
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Unknown microservice")));
    }

    @Test
    public void invalidOneTimePasswordShouldReturn403() throws Exception {
        scenario
            .given()
            .when().lease("divorce", "invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Invalid one-time password")));
    }

    @Test
    public void serviceNameShouldBeCaseInsensitive() throws Exception {
        scenario
            .given()
            .when().lease("DIvorCE", "invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Invalid one-time password")));
    }

    @Test
    public void validOneTimePasswordShouldReturnJwtToken() throws Exception {
        String validOneTimePassword = new TotpAuthenticator().issueOneTimePassword(divorceKey);

        scenario
            .given()
            .when().lease("divorce", validOneTimePassword)
            .then()
            .token(token -> {
                Claims claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody();
                assertThat(claims.getSubject()).isEqualTo("divorce");
            });
    }
}
