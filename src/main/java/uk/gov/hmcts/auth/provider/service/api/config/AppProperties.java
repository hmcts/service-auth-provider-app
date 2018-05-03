package uk.gov.hmcts.auth.provider.service.api.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static java.util.concurrent.TimeUnit.HOURS;

@ConfigurationProperties(prefix = "auth.provider.service.server")
public class AppProperties {

    private String jwtKey;

    private int ttlInSeconds = (int) HOURS.toSeconds(4);

    private Map<String, String> microserviceKeys;

    private RS256Config rs256 = new RS256Config();

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    public int getTtlInSeconds() {
        return ttlInSeconds;
    }

    public void setTtlInSeconds(int ttlInSeconds) {
        this.ttlInSeconds = ttlInSeconds;
    }

    public Map<String, String> getMicroserviceKeys() {
        return microserviceKeys;
    }

    public void setMicroserviceKeys(Map<String, String> microserviceKeys) {
        this.microserviceKeys = microserviceKeys;
    }

    public RS256Config getRs256() {
        return rs256;
    }

    public void setRs256(RS256Config rs256) {
        this.rs256 = rs256;
    }

    static class RS256Config {

        private boolean enabled;

        private String privateKey;

        private String publicKey;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }
}
