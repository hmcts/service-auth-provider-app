package uk.gov.hmcts.auth.provider.service.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtRS256Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.totp.GoogleTotpAuthenticator;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;


@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ServiceAuthProviderApplication {

    @Bean
    public TotpAuthenticator totpAuthenticator() {
        return new GoogleTotpAuthenticator();
    }

    @Bean
    public JwtTool jwtTool(ServiceAuthProviderApplicationConfig config) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (config.getRs256().isEnabled()) {
            return new JwtRS256Tool(
                config.getRs256().getPrivateKey(),
                config.getRs256().getPublicKey(),
                config.getTtlInSeconds()
            );
        } else {
            return new JwtHS512Tool(
                config.getJwtKey(),
                config.getTtlInSeconds(),
                Clock.systemDefaultZone()
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthProviderApplication.class, args);
    }
}
