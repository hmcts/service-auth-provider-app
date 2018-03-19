package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MicroserviceRepositoryTest {

    private final MicroserviceRepository repository = new MicroserviceRepository(ImmutableMap.of("ID", "key"));

    @Test(expected = UnknownMicroserviceException.class)
    public void throwsExceptionIfMicroserviceUnknown() {
        repository.findOne("unknown");
    }

    @Test
    public void returnsMicroservice() {
        assertThat(repository.findOne("id")).isEqualTo(new Microservice("id", "key"));
    }

    @Test
    public void returnsMicroserviceCaseInsensitive() {
        assertThat(repository.findOne("iD")).isEqualTo(new Microservice("id", "key"));
    }

}
