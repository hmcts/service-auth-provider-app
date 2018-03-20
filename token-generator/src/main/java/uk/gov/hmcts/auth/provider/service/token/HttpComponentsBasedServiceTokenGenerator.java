package uk.gov.hmcts.auth.provider.service.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import java.io.IOException;
import java.util.Map;

public class HttpComponentsBasedServiceTokenGenerator implements ServiceTokenGenerator {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final String microservice;
    private final TotpAuthenticator totpAuthenticator;
    private final String totpKey;

    public HttpComponentsBasedServiceTokenGenerator(HttpClient httpClient,
                                                    String baseUrl,
                                                    String microservice,
                                                    TotpAuthenticator totpAuthenticator,
                                                    String totpKey) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.microservice = microservice;
        this.totpAuthenticator = totpAuthenticator;
        this.totpKey = totpKey;
    }

    public String generate() {
        try {
            String oneTimePassword = totpAuthenticator.issueOneTimePassword(totpKey);

            Map<String, String> tokenDetails = ImmutableMap.of(
                "microservice", microservice,
                "oneTimePassword", oneTimePassword
            );

            HttpPost request = new HttpPost(baseUrl + "/lease");
            request.setEntity(
                new StringEntity(
                    new ObjectMapper()
                        .writeValueAsString(tokenDetails)
                ));

            request.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

            return httpClient.execute(request, httpResponse -> {
                checkStatusIs2xx(httpResponse);
                return "Bearer " + EntityUtils.toString(httpResponse.getEntity());
            });
        } catch (IOException e) {
            throw new ServiceTokenGenerationException(e);
        }
    }

    private void checkStatusIs2xx(HttpResponse httpResponse) throws IOException {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }
}
