import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SmokeTest {
    @Value("${TEST_URL:http://localhost:8489}")
    private String testUrl;

    @Before
    public void setup() {
        RestAssured.baseURI = testUrl;
    }

    @Test
    public void check_application_is_healthy() {
        System.out.println("ST executed");
        RestAssured.given()
            .relaxedHTTPSValidation()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .when().get("/health").then().statusCode(200);
    }
}
