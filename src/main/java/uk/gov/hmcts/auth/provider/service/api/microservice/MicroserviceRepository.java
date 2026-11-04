package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.warrenstrange.googleauth.HmacHashFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.config.AppProperties;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
@EnableConfigurationProperties(AppProperties.class)
public class MicroserviceRepository implements FindOne<Microservice> {

    private final Map<String, String> keys;
    private final Map<String, HmacHashFunction> otpAlgorithms;

    @Autowired
    public MicroserviceRepository(AppProperties appProperties) {
        this(appProperties.getMicroserviceKeys(), appProperties.getMicroserviceOtpAlgorithms());
    }

    public MicroserviceRepository(Map<String, String> microserviceKeys) {
        this(microserviceKeys, emptyMap());
    }

    public MicroserviceRepository(Map<String, String> microserviceKeys,
                                  Map<String, HmacHashFunction> otpAlgorithms) {
        if (microserviceKeys == null) {
            this.keys = emptyMap();
        } else {
            this.keys = microserviceKeys
                .entrySet()
                .stream()
                .collect(toMap(
                    entry -> entry.getKey().toLowerCase(Locale.ROOT).replace(".", "_"),
                    Map.Entry::getValue
                ));
        }
        this.otpAlgorithms = otpAlgorithms.entrySet().stream().collect(toMap(
            entry -> entry.getKey().toLowerCase(Locale.ROOT).replace(".", "_"),
            entry -> java.util.Objects.requireNonNull(entry.getValue())
        ));
        if (!this.keys.keySet().containsAll(this.otpAlgorithms.keySet())) {
            throw new IllegalArgumentException("OTP algorithm configured for an unknown microservice");
        }
    }

    @Override
    public Microservice findOne(String microservice) {
        String microserviceLowercase = microservice.toLowerCase(Locale.ROOT);

        if (!keys.containsKey(microserviceLowercase)) {
            throw new UnknownMicroserviceException();
        }

        return new Microservice(microserviceLowercase, keys.get(microserviceLowercase),
            otpAlgorithms.getOrDefault(microserviceLowercase, HmacHashFunction.HmacSHA1));
    }

    public List<String> getNames() {
        return keys.keySet().stream().sorted().collect(toList());
    }
}
