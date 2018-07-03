package uk.gov.hmcts.auth.provider.service.api.microservice;

import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.config.AppProperties;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
@EnableConfigurationProperties(AppProperties.class)
public class MicroserviceRepository implements FindOne<Microservice> {

    private final Map<String, String> keys;

    @Autowired
    public MicroserviceRepository(AppProperties appProperties) {
        this(appProperties.getMicroserviceKeys());
    }

    public MicroserviceRepository(Map<String, String> microserviceKeys) {
        if (microserviceKeys == null) {
            this.keys = emptyMap();
        } else {
            assertSecretsAreValid(microserviceKeys);
            this.keys = microserviceKeys
                .entrySet()
                .stream()
                .collect(toMap(
                    entry -> entry.getKey().toLowerCase(),
                    entry -> entry.getValue()
                ));
        }
    }

    @Override
    public Microservice findOne(String microservice) {
        String microserviceLowercase = microservice.toLowerCase();

        if (!keys.containsKey(microserviceLowercase)) {
            throw new UnknownMicroserviceException();
        }

        return new Microservice(microserviceLowercase, keys.get(microserviceLowercase));
    }

    private void assertSecretsAreValid(Map<String, String> microserviceKeys) {
        Base32 base32 = new Base32();
        Predicate<String> isValidSecret = s -> base32.isInAlphabet(s) && s.length() == 16;

        List<String> servicesWithInvalidSecrets =
            microserviceKeys
                .entrySet()
                .stream()
                .filter(entry -> !isValidSecret.test(entry.getValue()))
                .map(entry -> entry.getKey())
                .collect(toList());

        if (!servicesWithInvalidSecrets.isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid secrets in configuration for the following services: "
                    + String.join(", ", servicesWithInvalidSecrets)
            );
        }
    }
}
