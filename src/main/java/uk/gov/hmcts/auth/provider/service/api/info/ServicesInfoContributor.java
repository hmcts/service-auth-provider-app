package uk.gov.hmcts.auth.provider.service.api.info;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.microservice.MicroserviceRepository;

@Component
public class ServicesInfoContributor implements InfoContributor {

    private final MicroserviceRepository repo;

    public ServicesInfoContributor(MicroserviceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("services", repo.getNames());
    }
}
