package uk.gov.hmcts.auth.provider.service.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static java.util.Arrays.asList;

public class HttpComponentsBasedServiceTokenGenerator implements ServiceTokenGenerator {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final String microservice;
    private final TotpAuthenticator totpAuthenticator;
    private final String totpKey;
    private final String requestPath;

    public HttpComponentsBasedServiceTokenGenerator(HttpClient httpClient,
                                                    String baseUrl,
                                                    String microservice,
                                                    TotpAuthenticator totpAuthenticator,
                                                    String totpKey,
                                                    String requestPath) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.microservice = microservice;
        this.totpAuthenticator = totpAuthenticator;
        this.totpKey = totpKey;
        this.requestPath = requestPath;
    }

    public String generate() {
        try {
            String oneTimePassword = totpAuthenticator.issueOneTimePassword(totpKey);
            HttpPost request = new HttpPost(baseUrl + requestPath);

            if (requestPath.contains("v1")) {
                populateRequestBodyWithHeader(oneTimePassword, request);
            } else {
                request.setEntity(new UrlEncodedFormEntity(asList(
                    new BasicNameValuePair("microservice", microservice),
                    new BasicNameValuePair("oneTimePassword", oneTimePassword)
                )));
            }

            return httpClient.execute(request, httpResponse -> {
                checkStatusIs2xx(httpResponse);
                return "Bearer " + EntityUtils.toString(httpResponse.getEntity());
            });
        } catch (IOException e) {
            throw new ServiceTokenGenerationException(e);
        }

    }

    private void populateRequestBodyWithHeader(String oneTimePassword, HttpPost request) throws UnsupportedEncodingException, JsonProcessingException {
        Map<String, String> tokenDetails = ImmutableMap.of(
            "microservice", microservice,
            "oneTimePassword", oneTimePassword
        );

        request.setEntity(
            new StringEntity(
                new ObjectMapper()
                    .writeValueAsString(tokenDetails)
            ));

        request.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
    }

    private void checkStatusIs2xx(HttpResponse httpResponse) throws IOException {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }
}
