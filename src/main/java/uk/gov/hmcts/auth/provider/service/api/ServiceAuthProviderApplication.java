package uk.gov.hmcts.auth.provider.service.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class ServiceAuthProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthProviderApplication.class, args);
    }
}
