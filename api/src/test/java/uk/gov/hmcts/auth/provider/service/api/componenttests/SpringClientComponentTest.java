package uk.gov.hmcts.auth.provider.service.api.componenttests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenGenerator;
import uk.gov.hmcts.auth.provider.service.token.ServiceTokenParser;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringClientComponentTest extends ComponentTestBase {

    @Autowired
    @Qualifier("serviceTokenGenerator")
    private ServiceTokenGenerator generator;
    @Autowired
    private ServiceTokenParser parser;

    @Test
    public void leaseWorksWhenUsingOurClient() throws Exception {
        assertThat(generator.generate()).isNotEmpty();
    }

    @Test
    public void leaseAndAuthenticateWorksWhenUsingOurClient() throws Exception {
        String subject = parser.parse(generator.generate());
        assertThat(subject).isEqualTo("divorce");
    }

}
