package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.List;
import com.warrenstrange.googleauth.HmacHashFunction;

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
    @Test
    public void algorithmIsControlledByServerConfiguration() {
        MicroserviceRepository repo = new MicroserviceRepository(
            ImmutableMap.of("CCPAY.GW", "secret", "legacy", "other"),
            ImmutableMap.of("CCPAY.GW", HmacHashFunction.HmacSHA256)
        );
        assertThat(repo.findOne("CCPAY_GW").otpAlgorithm).isEqualTo(HmacHashFunction.HmacSHA256);
        assertThat(repo.findOne("legacy").otpAlgorithm).isEqualTo(HmacHashFunction.HmacSHA1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void misspelledServiceConfigurationFailsAtStartup() {
        new MicroserviceRepository(ImmutableMap.of("real", "key"),
            ImmutableMap.of("typo", HmacHashFunction.HmacSHA256));
    }
}
