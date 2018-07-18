package uk.gov.hmcts.auth.provider.service.api.componenttests;

import org.junit.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InfoEndpointTest extends ComponentTestBase {

    @Test
    public void should_return_names_of_configured_services() throws Exception {
        mvc
            .perform(get("/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath(
                "$.services",
                containsInAnyOrder("cmc", "divorce")) // see config for tests
            );
    }
}
