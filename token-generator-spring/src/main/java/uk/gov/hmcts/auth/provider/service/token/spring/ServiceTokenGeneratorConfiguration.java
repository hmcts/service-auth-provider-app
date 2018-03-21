package uk.gov.hmcts.auth.provider.service.token.spring;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.auth.provider.service.token.CachedServiceTokenGenerator;
import uk.gov.hmcts.auth.provider.service.token.HttpComponentsBasedServiceTokenGenerator;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenGenerator;
import uk.gov.hmcts.auth.totp.StrangeTotpAuthenticator;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

@Configuration
@Lazy
public class ServiceTokenGeneratorConfiguration {


    @Bean
    @ConditionalOnMissingBean(TotpAuthenticator.class)
    public TotpAuthenticator totpAuthenticator() {
        return new StrangeTotpAuthenticator();
    }

    @Bean
    public HttpClient serviceTokenGeneratorHttpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    @ConditionalOnMissingBean(name = "serviceTokenGenerator")
    public ServiceTokenGenerator serviceTokenGenerator(HttpClient serviceTokenGeneratorHttpClient,
                                                       @Value("${auth.provider.service.client.baseUrl}") String baseUrl,
                                                       @Value("${auth.provider.service.client.microservice}") String microservice,
                                                       TotpAuthenticator totpAuthenticator,
                                                       @Value("${auth.provider.service.client.key}") String key) {

        return new HttpComponentsBasedServiceTokenGenerator(serviceTokenGeneratorHttpClient, baseUrl, microservice, totpAuthenticator, key);
    }

    @Bean
    @ConditionalOnMissingBean(name = "cachedServiceTokenGenerator")
    public ServiceTokenGenerator cachedServiceTokenGenerator(@Qualifier("serviceTokenGenerator") ServiceTokenGenerator serviceTokenGenerator,
                                                             @Value("${auth.provider.service.client.tokenTimeToLiveInSeconds:3600}") int ttl) {
        return new CachedServiceTokenGenerator(serviceTokenGenerator, ttl);
    }
}
