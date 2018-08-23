package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.List;

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
    public void getNames_should_return_names_of_configured_services_in_alphabetical_order() {
        //given
        MicroserviceRepository repo =
            new MicroserviceRepository(ImmutableMap.of(
                "b", "1",
                "c", "2",
                "a", "3",
                "d", "4"
            ));

        // when
        List<String> names = repo.getNames();

        // then
        assertThat(names).containsExactly("a", "b", "c", "d");
    }
}
