package uk.gov.hmcts.auth.provider.service.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtHS512Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtRS256Tool;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;

@Configuration
public class JwtConfiguration {

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
}
