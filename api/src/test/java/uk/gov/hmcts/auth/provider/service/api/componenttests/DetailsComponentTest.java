package uk.gov.hmcts.auth.provider.service.api.componenttests;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.error.ErrorDto;

import static org.assertj.core.api.Assertions.assertThat;

public class DetailsComponentTest extends ComponentTestBase {

    @Value("${auth.provider.service.server.jwtKey}")
    private String jwtKey;


    @Test
    public void headerWithoutBearerPrefixShouldReturn403() throws Exception {
        scenario
            .given()
            .when().details("Bearer-invalid")
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Invalid authorization header")));
    }

    @Test
    public void invalidJwtSignatureShouldReturn403() throws Exception {
        String someOtherKey = BaseEncoding.base64().encode(MacProvider.generateKey().getEncoded());
        String jwt = Jwts.builder().setSubject("divorce").signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, someOtherKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Invalid token signature")));
    }

    @Test
    public void expiredJwtSignatureShouldReturn403() throws Exception {
        String jwt = Jwts.builder().setSubject("divorce").setExpiration(new Date(0)).signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, jwtKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Token expired")));
    }

    @Test
    public void malformedJwtShouldReturn403() throws Exception {
        scenario
            .given()
            .when().details("Bearer malformed")
            .then().unauthorized(actual -> assertThat(actual).isEqualTo(new ErrorDto("Error verifying token")));
    }

    @Test
    public void validJwtShouldReturnSubject() throws Exception {
        String jwt = Jwts.builder().setSubject("divorce").signWith(JwtHS512Tool.SIGNATURE_ALGORITHM, jwtKey).compact();

        scenario
            .given()
            .when().details("Bearer " + jwt)
            .then().subject(actual -> assertThat(actual).isEqualTo("divorce"));
    }
}
