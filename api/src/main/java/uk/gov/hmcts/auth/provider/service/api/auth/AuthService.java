package uk.gov.hmcts.auth.provider.service.api.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidOneTimePasswordException;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.provider.service.api.microservice.FindOne;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;
import uk.gov.hmcts.auth.totp.TotpAuthenticator;

@Slf4j
@Component
public class AuthService {

    private final JwtTool jwtTool;
    private final FindOne<Microservice> microserviceRepository;
    private final TotpAuthenticator totpAuthenticator;

    @Autowired
    public AuthService(
        JwtTool jwtTool,
        FindOne<Microservice> microserviceRepository,
        TotpAuthenticator totpAuthenticator
    ) {
        this.jwtTool = jwtTool;
        this.microserviceRepository = microserviceRepository;
        this.totpAuthenticator = totpAuthenticator;
    }

    /**
     * Generates a new JWT token for given service.
     */
    public String lease(String microserviceId, String oneTimePassword) {
        Microservice microservice = microserviceRepository.findOne(microserviceId);

        if (totpAuthenticator.isOneTimePasswordValid(microservice.getKey(), oneTimePassword)) {
            return jwtTool.issueTokenForSubject(microservice.getId());
        } else {
            log.info("Invalid one-time password");
            throw new InvalidOneTimePasswordException();
        }
    }

    /**
     * Checks whether given token is valid. Returns service name.
     */
    public String verify(String jwt) {
        return jwtTool.verifyAndExtractSubject(jwt);
    }
}
