package uk.gov.hmcts.auth.provider.service.token.spring;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.auth.provider.service.token.HttpComponentsBasedServiceTokenParser;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenParser;

@Configuration
@Lazy
public class ServiceTokenParserConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "serviceTokenParserHttpClient")
    public HttpClient serviceTokenParserHttpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    public ServiceTokenParser serviceAuthProviderAuthCheckClient(HttpClient serviceTokenParserHttpClient,
                                                                 @Value("${auth.provider.service.client.baseUrl}") String baseUrl) {

        return new HttpComponentsBasedServiceTokenParser(serviceTokenParserHttpClient, baseUrl);
    }

}
