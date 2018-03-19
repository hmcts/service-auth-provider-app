package uk.gov.hmcts.auth.provider.service.token;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpComponentsBasedServiceTokenGeneratorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private HttpComponentsBasedServiceTokenGenerator client;

    @Before
    public void setUp() throws Exception {
        client = new HttpComponentsBasedServiceTokenGenerator(
            HttpClients.createMinimal(),
            "http://localhost:" + wireMockRule.port(),
            "microservice",
            new TotpAuthenticator() {
                @Override
                public String issueOneTimePassword(String key) {
                    return key.equals("key") ? "oneTimePassword" : null;
                }

                @Override
                public boolean isOneTimePasswordValid(String key, String password) {
                    return false;
                }
            },
            "key"
        );
    }

    @Test
    public void happyPath() {
        stubFor(
            post(urlEqualTo("/lease")).withRequestBody(matching("microservice=microservice&oneTimePassword=oneTimePassword"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("jwt"))
        );

        String jwt = client.generate();

        assertThat(jwt).isEqualTo("Bearer jwt");
    }

    @Test(expected = ServiceTokenGenerationException.class)
    public void non2xxResponseShouldResultInException() {
        stubFor(
            post(urlEqualTo("/lease")).withRequestBody(matching("microservice=microservice&oneTimePassword=oneTimePassword"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withBody("jwt"))
        );

        client.generate();
    }
}
