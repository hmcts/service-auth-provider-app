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
@EnableConfigurationProperties(AppProperties.class)
public class JwtConfiguration {

    @Autowired
    private AppProperties appProperties;

    @Bean
    public JwtTool jwtTool() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (appProperties.getRs256().isEnabled()) {
            return new JwtRS256Tool(
                appProperties.getRs256().getPrivateKey(),
                appProperties.getRs256().getPublicKey(),
                appProperties.getTtlInSeconds()
            );
        } else {
            return new JwtHS512Tool(
                appProperties.getJwtKey(),
                appProperties.getTtlInSeconds(),
                Clock.systemDefaultZone()
            );
        }
    }
}
