package uk.gov.hmcts.auth.provider.service.api.microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.config.AppProperties;

import java.util.Map;

import static java.util.Collections.emptyMap;
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
            this.keys = microserviceKeys
                .entrySet()
                .stream()
                .collect(toMap(
                    entry -> entry.getKey().toLowerCase().replace(".", "_"),
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
}
