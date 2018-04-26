package uk.gov.hmcts.auth.provider.service.api.auth.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TotpAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(TotpAuthenticator.class);

    private final IGoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    public String issueOneTimePassword(String base32Key) {
        return String.valueOf(googleAuthenticator.getTotpPassword(base32Key));
    }

    public boolean isOneTimePasswordValid(String base32Key, String token) {
        try {
            return googleAuthenticator.authorize(base32Key, Integer.parseInt(token));
        } catch (NumberFormatException exc) {
            logger.warn("Could not convert token string into integer", exc);
            return false;
        }
    }
}
