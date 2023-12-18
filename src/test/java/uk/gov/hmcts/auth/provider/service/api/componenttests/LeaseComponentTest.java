package uk.gov.hmcts.auth.provider.service.api.componenttests;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void should_be_able_to_provide_text_plain_error_if_required_by_the_client() throws Exception {
        mvc
            .perform(post("/lease")
                .content("invalid content to trigger error...")
                .accept(MediaType.TEXT_PLAIN_VALUE)
            )
            .andExpect(status().is(not(NOT_ACCEPTABLE.value())));
    }
}
