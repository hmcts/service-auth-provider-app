package uk.gov.hmcts.auth.totp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotpAuthenticatorImpl implements TotpAuthenticator {

    private static final Logger logger = LoggerFactory.getLogger(TotpAuthenticatorImpl.class);
    private final IGoogleAuthenticator googleAuthenticator;

    public TotpAuthenticatorImpl() {
        this(new GoogleAuthenticator());
    }

    public TotpAuthenticatorImpl(IGoogleAuthenticator googleAuthenticator) {
        this.googleAuthenticator = googleAuthenticator;
    }

    @Override
    public String issueOneTimePassword(String base32Key) {
        return String.valueOf(googleAuthenticator.getTotpPassword(base32Key));
    }

    @Override
    public boolean isOneTimePasswordValid(String base32Key, String token) {
        try {
            return googleAuthenticator.authorize(base32Key, Integer.parseInt(token));
        } catch (NumberFormatException e) {
            logger.warn("Could not convert token string into integer");
            return false;
        }
    }
}
