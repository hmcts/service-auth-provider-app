package uk.gov.hmcts.auth.provider.service.api.componenttests;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DetailsComponentTest extends ComponentTestBase {

    @Value("${jwtKey}")
    private String jwtKey;


    @Test
    public void headerWithoutBearerPrefixShouldReturn403() throws Exception {
        scenario
            .given()
            .when().details("Bearer-invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Invalid authorization header")));
    }

    @Test
    public void invalidJwtSignatureShouldReturn403() throws Exception {
        String someOtherKey = BaseEncoding.base64().encode(MacProvider.generateKey().getEncoded());
        String jwt = Jwts.builder().setSubject("divorce").signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, someOtherKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Invalid token signature")));
    }

    @Test
    public void expiredJwtSignatureShouldReturn403() throws Exception {
        String jwt = Jwts.builder().setSubject("divorce").setExpiration(new Date(0)).signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, jwtKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Token expired")));
    }

    @Test
    public void malformedJwtShouldReturn403() throws Exception {
        scenario
            .given()
            .when().details("Bearer malformed")
            .then().unauthorized(actual -> assertThat(actual).isEqualToComparingFieldByField(new ErrorDto("Error verifying token")));
    }

    @Test
    public void validJwtShouldReturnSubject() throws Exception {
        String jwt = Jwts.builder().setSubject("divorce").signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, jwtKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().subject(actual -> assertThat(actual).isEqualTo("divorce"));
    }

    @Test
    public void should_be_able_to_provide_text_plain_error_if_required_by_the_client() throws Exception {
        mvc
            .perform(get("/details")
                .content("invalid content to trigger error...")
                .accept(MediaType.TEXT_PLAIN_VALUE)
            )
            .andExpect(status().is(not(NOT_ACCEPTABLE.value())));
    }
}
