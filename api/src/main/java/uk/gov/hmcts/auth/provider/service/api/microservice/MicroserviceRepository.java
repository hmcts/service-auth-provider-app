package uk.gov.hmcts.auth.provider.service.api.microservice;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.ServiceAuthProviderApplicationConfig;

import static java.util.stream.Collectors.toMap;

@Component
public class MicroserviceRepository implements FindOne<Microservice> {
    private final Map<String, String> keys;

    @Autowired
    public MicroserviceRepository(ServiceAuthProviderApplicationConfig serviceAuthProviderApplicationConfig) {
        this(serviceAuthProviderApplicationConfig.getMicroserviceKeys());
    }

    public MicroserviceRepository(Map<String, String> microserviceKeys) {
        keys = microserviceKeys
            .entrySet().stream()
            .collect(toMap(entry -> entry.getKey().toLowerCase(), Map.Entry::getValue));
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
