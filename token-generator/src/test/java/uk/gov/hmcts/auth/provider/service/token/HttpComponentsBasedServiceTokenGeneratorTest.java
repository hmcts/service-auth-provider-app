package uk.gov.hmcts.auth.provider.service.token;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpComponentsBasedServiceTokenGeneratorTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private HttpComponentsBasedServiceTokenGenerator client;

    @Test
    public void happyPath() {
        createHttpComponentsBasedServiceTokenGenerator("/lease");

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
        createHttpComponentsBasedServiceTokenGenerator("/lease");

        stubFor(
            post(urlEqualTo("/lease")).withRequestBody(matching("microservice=microservice&oneTimePassword=oneTimePassword"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withBody("jwt"))
        );

        client.generate();
    }

    @Test
    public void happyPathWithVersion1() {
        createHttpComponentsBasedServiceTokenGenerator("/v1/lease");

        stubFor(
            post(urlEqualTo("/v1/lease")).withRequestBody(equalToJson("{\"microservice\": \"microservice\",\"oneTimePassword\": \"oneTimePassword\"}"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withBody("jwt"))
        );

        String jwt = client.generate();

        assertThat(jwt).isEqualTo("Bearer jwt");
    }

    @Test(expected = ServiceTokenGenerationException.class)
    public void non2xxResponseShouldResultInExceptionWithVersion1() {
        createHttpComponentsBasedServiceTokenGenerator("/v1/lease");

        stubFor(
            post(urlEqualTo("/v1/lease")).withRequestBody(equalToJson("{\"microservice\": \"microservice\",\"oneTimePassword\": \"oneTimePassword\"}"))
                .willReturn(aResponse()
                    .withStatus(400)
                    .withBody("jwt"))
        );

        client.generate();
    }

    private void createHttpComponentsBasedServiceTokenGenerator(String requestPath) {
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
            "key",
            requestPath
        );
    }

}
