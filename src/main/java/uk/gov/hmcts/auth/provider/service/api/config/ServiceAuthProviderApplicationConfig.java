package uk.gov.hmcts.auth.provider.service.api.config;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.HOURS;

@Data
@Component
@ConfigurationProperties(prefix = "auth.provider.service.server")
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAuthProviderApplicationConfig {
    private String jwtKey;
    private int ttlInSeconds = (int) HOURS.toSeconds(4);
    private Map<String, String> microserviceKeys;
    private RS256Config rs256 = new RS256Config(false, null, null);

    @Data
    @AllArgsConstructor
    public static class RS256Config {
        private boolean enabled;
        private String privateKey;
        private String publicKey;
    }
}
