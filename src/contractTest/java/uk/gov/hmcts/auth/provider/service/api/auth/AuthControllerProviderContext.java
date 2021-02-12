package uk.gov.hmcts.auth.provider.service.api.auth;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.microservice.FindOne;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;

@Configuration
public class AuthControllerProviderContext {

    @MockBean
    JwtTool jwtTool;
    @MockBean
    FindOne<Microservice> microserviceRepository;
    @MockBean
    TotpAuthenticator totpAuthenticator;

    @Primary
    @Bean
    public AuthService authService(){
        return new AuthService(jwtTool, microserviceRepository, totpAuthenticator);
    }
}
