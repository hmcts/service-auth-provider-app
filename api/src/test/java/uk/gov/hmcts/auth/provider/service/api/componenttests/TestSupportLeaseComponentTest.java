package uk.gov.hmcts.auth.provider.service.api.componenttests;

import org.junit.Test;

public class TestSupportLeaseComponentTest extends ComponentTestBase {

    @Test
    public void should_generate_jwt_for_service() throws Exception {
        String serviceName = "some_arbitrary_service_name";

        String token =
            scenario
                .given()
                .when()
                .testSupportLease(serviceName)
                .then()
                .andReturn()
                .getResponse()
                .getContentAsString();

        scenario
            .given()
            .when()
            .details("Bearer " + token)
            .then()
            .isOk(serviceName);
    }
}
