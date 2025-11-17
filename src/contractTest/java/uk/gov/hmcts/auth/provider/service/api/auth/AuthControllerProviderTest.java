package uk.gov.hmcts.auth.provider.service.api.auth;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.VersionSelector;
import au.com.dius.pact.provider.spring.spring6.Spring6MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.microservice.FindOne;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Provider("s2s_auth")
@PactBroker(
    url = "${PACT_BROKER_URL:https://pact-broker.platform.hmcts.net}",
    consumerVersionSelectors = {
        @VersionSelector(tag = "${PACT_BRANCH_NAME:Dev}")
    },
    providerTags = "${pactbroker.providerTags:master}",
    enablePendingPacts = "${pactbroker.enablePending:true}"
)
@ContextConfiguration(classes = AuthControllerProviderContext.class)
@IgnoreNoPactsToVerify
public class AuthControllerProviderTest {

    @Autowired
    private AuthService authService;

    @Autowired
    JwtTool jwtTool;
    @Autowired
    FindOne<Microservice> microserviceRepository;
    @Autowired
    TotpAuthenticator totpAuthenticator;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }


    @BeforeEach
    void before(PactVerificationContext context) {
        System.getProperties().setProperty("pact.verifier.publishResults", "true");
        Spring6MockMvcTestTarget testTarget = new Spring6MockMvcTestTarget();
        testTarget.setControllers(new AuthController(authService));
        context.setTarget(testTarget);
    }

    @State({"microservice with valid credentials"})
    public void toSetUpValidMicroservice() {
        when(microserviceRepository.findOne(anyString())).thenReturn(new Microservice("someId", "someKey"));
        when(totpAuthenticator.isOneTimePasswordValid(anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(jwtTool.issueTokenForSubject(anyString())).thenReturn(
            "someMicroServiceToken");
    }

    @State({"microservice with valid token"})
    public void toSetUpMicroserviceToken() {
        when(jwtTool.verifyAndExtractSubject(anyString())).thenReturn(
            "someMicroServiceName");
    }
}
