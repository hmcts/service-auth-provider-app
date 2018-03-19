package uk.gov.hmcts.auth.provider.service.token;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import static java.util.Arrays.asList;

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

            HttpPost request = new HttpPost(baseUrl + "/lease");
            request.setEntity(new UrlEncodedFormEntity(asList(
                new BasicNameValuePair("microservice", microservice),
                new BasicNameValuePair("oneTimePassword", oneTimePassword)
            )));

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
