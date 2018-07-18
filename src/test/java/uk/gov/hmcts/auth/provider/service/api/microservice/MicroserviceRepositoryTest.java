package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class MicroserviceRepositoryTest {

    private final MicroserviceRepository repository = new MicroserviceRepository(ImmutableMap.of("ID", "key"));

    @Test(expected = UnknownMicroserviceException.class)
    public void throwsExceptionIfMicroserviceUnknown() {
        repository.findOne("unknown");
    }

    @Test
    public void returnsMicroservice() {
        assertThat(repository.findOne("id")).isEqualToComparingFieldByField(new Microservice("id", "key"));
    }

    @Test
    public void returnsMicroserviceCaseInsensitive() {
        assertThat(repository.findOne("iD")).isEqualToComparingFieldByField(new Microservice("id", "key"));
    }

    @Test
    public void getNames_should_return_names_of_configured_services() {
        //given
        MicroserviceRepository repo =
            new MicroserviceRepository(ImmutableMap.of(
                "a", "1",
                "b", "2",
                "c", "3"
            ));

        // when
        Set<String> names = repo.getNames();

        // then
        assertThat(names).containsExactlyInAnyOrder("a", "b", "c");
    }
}
