package uk.gov.hmcts.auth.provider.service.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtRS256Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;

@Configuration
@EnableConfigurationProperties(ServiceAuthProviderApplicationConfig.class)
public class JwtConfiguration {

    @Autowired
    private ServiceAuthProviderApplicationConfig providerProperties;

    @Bean
    public JwtTool jwtTool() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (providerProperties.getRs256().isEnabled()) {
            return new JwtRS256Tool(
                providerProperties.getRs256().getPrivateKey(),
                providerProperties.getRs256().getPublicKey(),
                providerProperties.getTtlInSeconds()
            );
        } else {
            return new JwtHS512Tool(
                providerProperties.getJwtKey(),
                providerProperties.getTtlInSeconds(),
                Clock.systemDefaultZone()
            );
        }
    }
}
