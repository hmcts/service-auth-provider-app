package uk.gov.hmcts.auth.provider.service.api.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.auth.provider.service.api.auth.exceptions.InvalidOneTimePasswordException;
import uk.gov.hmcts.auth.provider.service.api.auth.jwt.JwtTool;
import uk.gov.hmcts.auth.provider.service.api.auth.totp.TotpAuthenticator;
import uk.gov.hmcts.auth.provider.service.api.microservice.FindOne;
import uk.gov.hmcts.auth.provider.service.api.microservice.Microservice;

@Component
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

        if (totpAuthenticator.isOneTimePasswordValid(microservice.key, oneTimePassword)) {
            return jwtTool.issueTokenForSubject(microservice.id);
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
