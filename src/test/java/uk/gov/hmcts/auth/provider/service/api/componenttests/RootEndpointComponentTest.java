package uk.gov.hmcts.auth.provider.service.api.componenttests;

import org.junit.Test;

public class RootEndpointComponentTest extends ComponentTestBase {

    @Test
    public void should_welcome_upon_root_request_with_200_response_code() throws Exception {
        scenario
            .given()
            .when().root()
            .then().isOk("Welcome to Service Auth Provider");
    }
}
