package uk.gov.hmcts.auth.provider.service.api.microservice;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class MicroserviceRepositoryTest {

    private final MicroserviceRepository repository = new MicroserviceRepository(
        ImmutableMap.of(
            "sample_service", "AAAAAAAAAAAAAAAA",
            "other_service", "BBBBBBBBBBBBBBBB"
        )
    );

    @Test(expected = UnknownMicroserviceException.class)
    public void throwsExceptionIfMicroserviceUnknown() {
        repository.findOne("unknown");
    }

    @Test
    public void returnsMicroservice() {
        assertThat(repository.findOne("sample_service"))
            .isEqualToComparingFieldByField(new Microservice("sample_service", "AAAAAAAAAAAAAAAA"));
    }

    @Test
    public void returnsMicroserviceCaseInsensitive() {
        assertThat(repository.findOne("SAMPLE_SERVICE"))
            .isEqualToComparingFieldByField(new Microservice("sample_service", "AAAAAAAAAAAAAAAA"));
    }

    @Test
    public void should_not_allow_configuring_services_with_invalid_secret() {
        ImmutableMap<String, String> config =
            ImmutableMap.of(
                "ok_1", "AAAAAAAAAAAAAAAA",
                "ok_2", "NBSWY3DPN5XW633P",
                "invalid_1", "rubbish",
                "invalid_2", "9999999999999999" // '9' is not in the base32 alphabet
            );

        Throwable exc = catchThrowable(() -> new MicroserviceRepository(config));

        assertThat(exc)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid secrets");

        assertThat(exc.getMessage())
            .doesNotContain("ok_1")
            .doesNotContain("ok_2")
            .contains("invalid_1")
            .contains("invalid_2");
    }
}
